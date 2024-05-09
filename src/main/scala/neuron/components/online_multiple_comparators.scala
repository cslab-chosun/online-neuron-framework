package neuron.components

import chisel3._
import chisel3.util._

import neuron.components._
import neuron.utils._

class ResultOfOnlineMinOrMax(
    isIndexBased: Boolean = false, // by should we return index of maximum value element or the value
    maximumNumberOfIndex: Int = DesignConsts.NUMBER_LENGTH
) extends Bundle {

  val minMaxResult = UInt(1.W)
  val earlyTerminate = UInt(1.W)
}

class OnlineMultipleComparator(
    debug: Boolean = DesignConsts.ENABLE_DEBUG,
    isMax: Boolean = true, // by default MAX Comparator
    isIndexBased: Boolean = true, // by should we return index of maximum value element or the value
    leastIndexFirst: Boolean = true, // by deafult least index is shown first (in case of equal operands)
    countOfInputs: Int = 0,
    maximumNumberOfIndex: Int = 10 // in case if isIndexBased == TRUE
    // (This shows the maximum number that is possible for an index to be)
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

    val inputs = Input(Vec(countOfInputs, UInt(1.W)))

    //
    // Output signals
    //
    val result = Output(UInt(1.W))
    val resultIndex = Output(UInt(log2Ceil(maximumNumberOfIndex).W))
    val resultValid = Output(Bool())
  })

  val maxMinOutput = Wire(UInt(1.W))
  val maxMinIndexOutput = WireInit(0.U(log2Ceil(maximumNumberOfIndex).W))

  var layerCompute = LayerCompute.Compute(debug)(countOfInputs)

  val delayCount = RegInit(0.U(layerCompute._2.W))
  val resultValid = WireInit(false.B)
  val regInit = RegInit(false.B)

  val regMinMaxResultVec = RegInit(
    VecInit(
      Seq.fill(layerCompute._1)(
        0.U(1.W)
      )
    )
  )

  val regMinMaxResultETVec = RegInit(
    VecInit(
      Seq.fill(layerCompute._1)(
        0.U(1.W)
      )
    )
  )

  when(regInit === false.B) {

    regInit := true.B
  }

  //////////////////////////////////////////////////////////////////

  when(regMinMaxResultVec(layerCompute._1 - 2) === 0.U) {
    maxMinOutput := 0.U
  }.otherwise {
    maxMinOutput := 1.U
  }

  //////////////////////////////////////////////////////////////////

  LogInfo(debug)(
    "final max layer (result vector index): " + (layerCompute._1 - 2)
  )

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
        val (
          resultSelectedInputFinal,
          resultEarlyTerminate1,
          resultEarlyTerminate2,
          resultMaxMinOutput
        ) = OnlineComparator2(
          false /*debug*/,
          isMax
        )(
          io.start,
          io.inputs(i * 2),
          io.inputs(i * 2 + 1),
          false.B
        )

        //
        // Connect the outputs
        //
        regMinMaxResultVec(i) := resultMaxMinOutput
        var resultEarlyTerminate = resultEarlyTerminate1 | resultEarlyTerminate2
        regMinMaxResultETVec(i) := resultEarlyTerminate

        LogInfo(debug)(
          "connecting inputs(" + (i * 2) + ") and input(" + (i * 2 + 1) + ") to regMinMaxResultVec(" + i + ")"
        )

      } else {

        //
        // *** Connect comparators ***
        //

        if (layerCompute._3 == true && i == layerCompute._1 - 1) {

          //
          // This is the last comparator with odd inputs
          //

          val (
            resultSelectedInputFinal,
            resultEarlyTerminate1,
            resultEarlyTerminate2,
            resultMaxMinOutput
          ) = OnlineComparator2(
            false /*debug*/,
            isMax
          )(
            io.start,
            regMinMaxResultVec(temp - 2),
            io.inputs(countOfInputs - 1),
            false.B
          )

          //
          // Connect the outputs
          //
          regMinMaxResultVec(i - 1) := resultMaxMinOutput
          var resultEarlyTerminate =
            resultEarlyTerminate1 | resultEarlyTerminate2
          regMinMaxResultETVec(i - 1) := resultEarlyTerminate

          LogInfo(debug)(
            "connecting odd (exceptional) regMinMaxResultVec(" + (temp - 2) + ") and inputs(" + (countOfInputs - 1) + ") to regMinMaxResultVec(" + (i - 1) + ")"
          )

        } else if (i != temp + 1) {

          val (
            resultSelectedInputFinal,
            resultEarlyTerminate1,
            resultEarlyTerminate2,
            resultMaxMinOutput
          ) = OnlineComparator2(
            false /*debug*/,
            isMax
          )(
            io.start,
            regMinMaxResultVec(temp),
            regMinMaxResultVec(temp + 1),
            false.B
          )

          //
          // Connect the outputs
          //
          regMinMaxResultVec(i) := resultMaxMinOutput
          var resultEarlyTerminate =
            resultEarlyTerminate1 | resultEarlyTerminate2
          regMinMaxResultETVec(i) := resultEarlyTerminate

          LogInfo(debug)(
            "connecting regMinMaxResultVec(" + temp + ") and regMinMaxResultVec(" + (temp + 1) + ") to regMinMaxResultVec(" + i + ")"
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
    maxMinOutput := 0.U
    maxMinIndexOutput := 0.U
    resultValid := false.B
  }

  //
  // Connect the outputs
  //
  io.result := maxMinOutput
  io.resultIndex := maxMinIndexOutput
  io.resultValid := resultValid

}

object OnlineMultipleComparator {

  def apply(
      debug: Boolean = DesignConsts.ENABLE_DEBUG,
      isMax: Boolean = true, // by default MAX Comparator
      isIndexBased: Boolean = false, // by should we return index of maximum value element or the value
      leastIndexFirst: Boolean = true, // by deafult least index is shown first (in case of equal operands)
      countOfInputs: Int = 0,
      maximumNumberOfIndex: Int = 10 // in case if isIndexBased == TRUE
      // (This shows the maximum number that is possible for an index to be)
  )(
      start: Bool,
      inputs: Vec[UInt]
  ): (UInt, Bool) = {

    val comparatorModule = Module(
      new OnlineMultipleComparator(
        debug,
        isMax,
        isIndexBased,
        leastIndexFirst,
        countOfInputs,
        maximumNumberOfIndex
      )
    )

    val result = Wire(UInt(1.W))
    val resultValid = Wire(Bool())

    //
    // Configure the input signals
    //
    comparatorModule.io.start := start
    comparatorModule.io.inputs := inputs

    result := comparatorModule.io.result
    resultValid := comparatorModule.io.resultValid

    //
    // Return the output result
    //
    (result, resultValid)
  }
}
