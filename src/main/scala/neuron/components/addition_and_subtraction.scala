package neuron.components

import chisel3._
import chisel3.util._

import neuron.utils._

class AdditionAndSubtraction(
    debug: Boolean = DesignConsts.ENABLE_DEBUG,
    isSum: Boolean = true, // by default SUM
    numberLength: Int = DesignConsts.NUMBER_LENGTH
) extends Module {

  val io = IO(new Bundle {

    //
    // Input signals
    //
    val start = Input(Bool())

    val in1 = Input(UInt(numberLength.W))
    val in2 = Input(UInt(numberLength.W))

    //
    // Output signals
    //
    val result = Output(UInt((numberLength + 1).W))
  })

  val resultOutput = WireInit(0.U((numberLength + 1).W))

  when(io.start === true.B) {

    if (isSum == true) {

      //
      // Addition
      //
      resultOutput := io.in1 + io.in2

    } else {

      //
      // Subtraction
      //
      resultOutput := io.in1 - io.in2

    }

  }

  //
  // Connect the outputs
  //
  io.result := resultOutput
}

object AdditionAndSubtraction {

  def apply(
      debug: Boolean = DesignConsts.ENABLE_DEBUG,
      isSum: Boolean = true, // by default SUM
      numberLength: Int = DesignConsts.NUMBER_LENGTH
  )(
      start: Bool,
      input1: UInt,
      input2: UInt
  ): UInt = {

    val additionAndSubtractionModule = Module(
      new AdditionAndSubtraction(debug, isSum, numberLength)
    )

    val result = Wire(UInt((numberLength + 1).W))

    //
    // Configure input signals
    //
    additionAndSubtractionModule.io.in1 := input1
    additionAndSubtractionModule.io.in2 := input2

    additionAndSubtractionModule.io.start := start

    //
    // Configure output signals
    //
    result := additionAndSubtractionModule.io.result

    result
  }
}
