package neuron.components

import chisel3._
import chisel3.util._

import neuron.components._
import neuron.utils._

class ResultOfMultipleMultiplicationAddition extends Bundle {
  val x = UInt(4.W) // Input
  val cin = Bool() // Input
  val z = UInt(2.W) // Output
  val cout = Bool() // Output
}

class OnlineMultipleMultiplicationAddition(
    debug: Boolean = DesignConsts.ENABLE_DEBUG,
    countOfInputs: Int = 0
) extends Module {

  //
  // Design constraints
  //
  require(countOfInputs >= 2)
  assert(
    countOfInputs >= 2,
    "err, the number of inputs should be at least 2."
  )

  val io = IO(new Bundle {

    //
    // Input signals
    //
    val start = Input(Bool())

    val inputs_xp = Input(Vec(countOfInputs, UInt(1.W)))
    val inputs_xn = Input(Vec(countOfInputs, UInt(1.W)))
    val inputs_yp = Input(Vec(countOfInputs, UInt(1.W)))
    val inputs_yn = Input(Vec(countOfInputs, UInt(1.W)))

    //
    // Output signals
    //
    val result = Output(UInt(2.W))
    val resultValid = Output(Bool())
  })

  val finalOutput = Wire(UInt(2.W))

  var layerCompute = ReductionLayerCompute.Compute(debug)(countOfInputs)

  val delayCount = RegInit(0.U(layerCompute._2.W))
  val resultValid = WireInit(false.B)

  val regVecZ = RegInit(
    VecInit(
      Seq.fill(layerCompute._1)(
        0.U(2.W)
      )
    )
  )

  //////////////////////////////////////////////////////////////////

  //
  // Connect output
  //
  finalOutput := regVecZ(layerCompute._1 - 2)

  LogInfo(debug)(
    "final layer (result vector index): " + (layerCompute._1 - 2)
  )
  //////////////////////////////////////////////////////////////////

  when(io.start) {

    //
    // Compute delay for validity of input
    //
    when(delayCount === layerCompute._2.U) {
      resultValid := true.B
    }
      .otherwise {
        delayCount := delayCount + 1.U
      }

    //
    // Implementation of the multiple comparator
    //
    var temp: Int = 0

    for (i <- 0 until layerCompute._1) {

      if ((layerCompute._3 == false && countOfInputs / 2 > i) || (layerCompute._3 == true && (countOfInputs - 1) / 2 > i)) {

        //
        // Connect inputs
        //
        val (zp, zn) = ROO4_Verilog(
          4 // width
        )(
          io.inputs_xp(i * 2).asBool,
          io.inputs_xn(i * 2 + 1).asBool,
          io.inputs_yp(i * 2).asBool,
          io.inputs_yn(i * 2 + 1).asBool
        )

        //
        // Connect the outputs
        //
        regVecZ(i) := Cat(zp.asUInt, zn.asUInt)

        LogInfo(debug)(
          "connecting inputs(" + (i * 2) + ") and input(" + (i * 2 + 1) + ") to regVecZ(" + i + ")"
        )

      } else {

        //
        // *** Connect comparators ***
        //

        if (layerCompute._3 == true && i == layerCompute._1 - 1) {

          //
          // This is the last comparator with odd inputs
          //

          //
          // *** never reach here since the input count would never be odd **
          //
          assert(true == true, "Error, input numbers cannot be odd")

        } else if (i != temp + 1) {

          val (z, cout) = OLSDA(
            false /*debug*/
          )(
            Cat(regVecZ(temp), regVecZ(temp + 1)),
            false.B // Carry is always zero
          )

          //
          // Connect the outputs (carry is ignored)
          //
          regVecZ(i) := z

          LogInfo(debug)(
            "connecting regVecZ(" + temp + ") and regVecZ(" + (temp + 1) + ") to regVecZ(" + i + ")"
          )
        }

        temp += 2
      }

    }

  }.otherwise {

    //
    // Reset the multiple comparator
    //
    delayCount := 0.U
    finalOutput := 0.U
    resultValid := false.B
  }

  //
  // Connect the outputs
  //
  io.result := finalOutput
  io.resultValid := resultValid

}

object OnlineMultipleMultiplicationAddition {

  def apply(
      debug: Boolean = DesignConsts.ENABLE_DEBUG,
      countOfInputs: Int = 0
  )(
      start: Bool,
      inputs_xp: Vec[UInt],
      inputs_xn: Vec[UInt],
      inputs_yp: Vec[UInt],
      inputs_yn: Vec[UInt]
  ): (UInt, Bool) = {

    val onlineMultipleMultiplicationAdditionModule = Module(
      new OnlineMultipleMultiplicationAddition(
        debug,
        countOfInputs
      )
    )

    val result = Wire(UInt(2.W))
    val resultValid = Wire(Bool())

    //
    // Configure the input signals
    //
    onlineMultipleMultiplicationAdditionModule.io.start := start

    onlineMultipleMultiplicationAdditionModule.io.inputs_xp := inputs_xp
    onlineMultipleMultiplicationAdditionModule.io.inputs_xn := inputs_xn
    onlineMultipleMultiplicationAdditionModule.io.inputs_yp := inputs_yp
    onlineMultipleMultiplicationAdditionModule.io.inputs_yn := inputs_yn

    result := onlineMultipleMultiplicationAdditionModule.io.result
    resultValid := onlineMultipleMultiplicationAdditionModule.io.resultValid

    //
    // Return the output result
    //
    (result, resultValid)
  }
}
