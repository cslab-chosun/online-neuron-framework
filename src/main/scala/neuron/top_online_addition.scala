package neuron

import chisel3._
import chisel3.util._

// _root_ disambiguates from package chisel3.util.circt if user imports chisel3.util._
import _root_.circt.stage.ChiselStage

import neuron.components._
import neuron.utils._

class AdditionBasedOnlineNeuronComputation(
    debug: Boolean = DesignConsts.ENABLE_DEBUG,
    countOfInputs: Int = 0
) extends Module {
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

  val firstDigit = RegInit(false.B)
  val returnZero = RegInit(false.B)

  val (outResult, outResultValid) =
    OnlineMultipleMultiplicationAddition(
      debug,
      countOfInputs
    )(
      io.start,
      io.inputs_xp,
      io.inputs_xn,
      io.inputs_yp,
      io.inputs_yn
    )

  //
  // Output delay registers
  //
  val delayedResult = RegNext(outResult)
  val delayedResultValid = RegNext(outResultValid)

  val outResultNeg = outResult(1)
  val outResultPos = outResult(0)

  //
  // Apply ReLU (rectified linear unit) conditions
  //
  when(outResultValid === true.B && firstDigit === false.B) {

    firstDigit := true.B

    when(outResultNeg === outResultPos || outResultNeg === 1.U) {
      returnZero := true.B
    }.otherwise {
      returnZero := false.B
    }

  }

  //
  // Connect output pins
  //
  io.resultValid := delayedResultValid

  //
  // Apply ReLU conditions
  //
  when(returnZero === true.B) {
    io.result := 0.U
  }.otherwise {
    io.result := delayedResult
  }

}

//-----------------------------------------------

object MainAdditionBasedOnlineCircuitGenerator extends App {

  val printAllVariations = true
  val printInitSeed: Double = 128
  var printCurrentVariation: Int = 0

  //
  // Generate verilog files
  //
  println(
    ChiselStage.emitSystemVerilog(
      new AdditionBasedOnlineNeuronComputation(
        true, // debug
        64 // countOfInputs
      ),
      firtoolOpts = Array(
        "-disable-all-randomization",
        // "-strip-debug-info",
        "--split-verilog", // The intention for this argument (and next argument) is to separate generated files.
        "-o",
        "generated/online/addition/"
      )
    )
  )

  // ------------------------------------------------------
  // Generate multiple variation of circuit
  //
  if (printAllVariations) {

    for (i <- 0 until 7) {

      printCurrentVariation = (printInitSeed * math.pow(2, i)).toInt

      LogInfo(true)("Generate number for: " + printCurrentVariation)

      // ------------------------------------------------------
      // Generate circuit for circuit with (printCurrentVariation) inputs
      //
      println(
        ChiselStage.emitSystemVerilog(
          new AdditionBasedOnlineNeuronComputation(
            true, // debug
            printCurrentVariation // countOfInputs
          ),
          firtoolOpts = Array(
            "-disable-all-randomization",
            // "-strip-debug-info",
            "--split-verilog", // The intention for this argument (and next argument) is to separate generated files.
            "-o",
            s"generated/online/var/$printCurrentVariation"
          )
        )
      )
    }
  }
}
