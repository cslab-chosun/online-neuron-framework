package neuron.components

import chisel3._
import chisel3.util._

import neuron.utils._

class MultiplicationAndDivision(
    debug: Boolean = DesignConsts.ENABLE_DEBUG,
    isMultiplication: Boolean = true, // by default MUL
    numberLength: Int = DesignConsts.NUMBER_LENGTH
) extends Module {

  val io = IO(new Bundle {

    //
    // Input signals
    //
    val start = Input(Bool())

    val in1 = Input(SInt(numberLength.W))
    val in2 = Input(SInt(numberLength.W))

    //
    // Output signals
    //
    val result = Output(SInt((2 * numberLength).W))
  })

  val resultOutput = WireInit(0.S((2 * numberLength).W))

  when(io.start === true.B) {

    if (isMultiplication == true) {

      //
      // Addition
      //
      resultOutput := io.in1 * io.in2

    } else {

      //
      // Subtraction
      //
      resultOutput := io.in1 / io.in2

    }

  }

  //
  // Connect the outputs
  //
  io.result := resultOutput
}

object MultiplicationAndDivision {

  def apply(
      debug: Boolean = DesignConsts.ENABLE_DEBUG,
      isMultiplication: Boolean = true, // by default MUL
      numberLength: Int = DesignConsts.NUMBER_LENGTH
  )(
      start: Bool,
      input1: SInt,
      input2: SInt
  ): SInt = {

    val multiplicationAndDivisionModule = Module(
      new MultiplicationAndDivision(debug, isMultiplication, numberLength)
    )

    val result = Wire(SInt((2 * numberLength).W))

    //
    // Configure input signals
    //
    multiplicationAndDivisionModule.io.in1 := input1
    multiplicationAndDivisionModule.io.in2 := input2

    multiplicationAndDivisionModule.io.start := start

    //
    // Configure output signals
    //
    result := multiplicationAndDivisionModule.io.result

    result
  }
}
