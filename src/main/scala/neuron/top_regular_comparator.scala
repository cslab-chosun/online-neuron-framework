package neuron

import chisel3._
import chisel3.util._

// _root_ disambiguates from package chisel3.util.circt if user imports chisel3.util._
import _root_.circt.stage.ChiselStage

import neuron.components._
import neuron.utils._

class ComparatorBasedRegularNeuronComputation(
    debug: Boolean = DesignConsts.ENABLE_DEBUG,
    isMax: Boolean = true, // by default MAX Comparator
    isIndexBased: Boolean = false, // by should we return index of maximum value element or the value
    leastIndexFirst: Boolean = true, // by deafult least index is shown first (in case of equal operands)
    numberLength: Int = DesignConsts.NUMBER_LENGTH,
    countOfInputs: Int = 0,
    maximumNumberOfIndex: Int = 10 // in case if isIndexBased == TRUE
    // (This shows the maximum number that is possible for an index to be)
) extends Module {
  val io = IO(new Bundle {

    //
    // Input signals
    //
    val start = Input(Bool())

    val inputs = Input(Vec(countOfInputs, UInt(numberLength.W)))

    //
    // Output signals
    //
    val result = Output(UInt(numberLength.W))

  })

  val outResult =
    MultipleComparator(
      debug,
      isMax,
      isIndexBased,
      leastIndexFirst,
      numberLength,
      countOfInputs,
      maximumNumberOfIndex
    )(
      io.start,
      io.inputs
    )

  //
  // Connect output pin
  //
  io.result := outResult

}

//-----------------------------------------------

object MainComparatorBasedRegularCircuitGenerator extends App {

  //
  // Generate verilog files
  //
  println(
    ChiselStage.emitSystemVerilog(
      new ComparatorBasedRegularNeuronComputation(
        true, // debug
        true, // isMax
        false, // isIndexBased
        true, // leastIndexFirst
        32, // numberLength
        64, // countOfInputs
        10 // maximumNumberOfIndex (not used)
      ),
      firtoolOpts = Array(
        "-disable-all-randomization",
        // "-strip-debug-info",
        "--split-verilog", // The intention for this argument (and next argument) is to separate generated files.
        "-o",
        "generated/regular/comparator/"
      )
    )
  )
}
