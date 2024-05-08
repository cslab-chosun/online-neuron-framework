package fuzzy

import chisel3._
import chisel3.util._

// _root_ disambiguates from package chisel3.util.circt if user imports chisel3.util._
import _root_.circt.stage.ChiselStage

import fuzzy.components._
import fuzzy.utils._

class OnlineNeuronComputation(
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

    val inputs = Input(Vec(countOfInputs, UInt(1.W)))

    //
    // Output signals
    //
    val result = Output(UInt(1.W))
    val resultValid = Output(Bool())

  })

  val (outResult, outResultValid) =
    OnlineMultipleComparator(
      debug,
      isMax,
      isIndexBased,
      leastIndexFirst,
      countOfInputs,
      maximumNumberOfIndex
    )(
      io.start,
      io.inputs
    )

  //
  // Connect output pins
  //
  io.result := outResult
  io.resultValid := outResultValid

}

//-----------------------------------------------

object MainOnlineCircuitGenerator extends App {

  //
  // Generate verilog files
  //
  println(
    ChiselStage.emitSystemVerilog(
      new OnlineNeuronComputation(
        true, // debug
        true, // isMax
        true, // isIndexBased
        true, // leastIndexFirst
        8, // numberLength
        5, // countOfInputs
        10 // maximumNumberOfIndex
      ),
      firtoolOpts = Array(
        "-disable-all-randomization",
        // "-strip-debug-info",
        "--split-verilog", // The intention for this argument (and next argument) is to separate generated files.
        "-o",
        "generated/online/"
      )
    )
  )
}
