package neuron

import chisel3._
import chisel3.util._

// _root_ disambiguates from package chisel3.util.circt if user imports chisel3.util._
import _root_.circt.stage.ChiselStage

import neuron.components._
import neuron.utils._

class AdditionBasedRegularNeuronComputation(
    debug: Boolean = DesignConsts.ENABLE_DEBUG,
    numberLength: Int = DesignConsts.NUMBER_LENGTH,
    countOfInputs: Int = 0
) extends Module {
  val io = IO(new Bundle {

    //
    // Input signals
    //
    val start = Input(Bool())

    val inputs = Input(Vec(countOfInputs, SInt(numberLength.W)))

    //
    // Output signals
    //
    val result = Output(SInt((2 * numberLength + log2Ceil(countOfInputs)).W))
    val resultValid = Output(Bool())

  })

  val (result, resultValid) =
    MultipleMultiplicationAddition(
      debug,
      true, // isSum
      numberLength,
      countOfInputs
    )(
      io.start,
      io.inputs
    )

  //
  // Connect output pins ()
  //
  io.resultValid := resultValid

  //
  // Apply ReLU (rectified linear unit) logic
  //
  when(result >= 0.S) {
    io.result := result
  }.otherwise {
    io.result := 0.S
  }

}

//-----------------------------------------------

object MainAdditionBasedRegularCircuitGenerator extends App {

  val printAllVariations = true
  val printInitSeed: Double = 128
  var printCurrentVariation: Int = 0

  //
  // Generate verilog files
  //
  println(
    ChiselStage.emitSystemVerilog(
      new AdditionBasedRegularNeuronComputation(
        true, // debug
        32, // numberLength
        64 // countOfInputs
      ),
      firtoolOpts = Array(
        "-disable-all-randomization",
        // "-strip-debug-info",
        "--split-verilog", // The intention for this argument (and next argument) is to separate generated files.
        "-o",
        "generated/regular/addition/"
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
          new AdditionBasedRegularNeuronComputation(
            true, // debug
            32, // numberLength
            printCurrentVariation // countOfInputs
          ),
          firtoolOpts = Array(
            "-disable-all-randomization",
            // "-strip-debug-info",
            "--split-verilog", // The intention for this argument (and next argument) is to separate generated files.
            "-o",
            s"generated/regular/var/$printCurrentVariation"
          )
        )
      )
    }
  }
}
