package neuron.components

import chisel3._
import chisel3.util._

import neuron.utils._

class ResultOfMultiplicationAddition(
    numberLength: Int = DesignConsts.NUMBER_LENGTH
) extends Bundle {

  val multiplicationAdditionionResult = SInt(numberLength.W)

}

object ReductionLayerCompute {

  def Compute(debug: Boolean = DesignConsts.ENABLE_DEBUG)(
      countOfInputs: Int
  ): (Int, Int, Boolean) = {

    //
    // Check if number of inputs is odd or event
    //
    var isOddNumberOfInputs: Boolean = false
    var tempRemainedInputCount: Int = countOfInputs
    var tempMultiplicatorOrAdderCount: Int = 0
    var delayCycles: Int = 0

    if (countOfInputs % 2 == 1) {
      //
      // The number of inputs are odd
      //
      isOddNumberOfInputs = true
      tempRemainedInputCount = tempRemainedInputCount - 1
    }

    while (tempRemainedInputCount != 1) {

      if (tempRemainedInputCount % 2 == 1) {
        tempRemainedInputCount -= 1
        tempMultiplicatorOrAdderCount += 1
        delayCycles += 1
      }

      tempRemainedInputCount = tempRemainedInputCount / 2
      tempMultiplicatorOrAdderCount += tempRemainedInputCount
      delayCycles += 1
    }

    //
    // if it's odd then we should consider another comparison too
    //
    if (isOddNumberOfInputs) {
      tempMultiplicatorOrAdderCount = tempMultiplicatorOrAdderCount + 1
      delayCycles = delayCycles + 1
    }
    LogInfo(debug)("multiple multiplication/addition | count of inputs: " + countOfInputs)
    LogInfo(debug)(
      "multiple multiplication/addition | number of needed multiplication/addition: " + tempMultiplicatorOrAdderCount
    )
    LogInfo(debug)("multiple multiplication/addition | delay cycles: " + delayCycles)

    //
    // Return the number of needed multiplicators/adders and delays
    //
    (tempMultiplicatorOrAdderCount + 1, delayCycles, isOddNumberOfInputs)
  }
}

class MultipleMultiplicationAddition(
    debug: Boolean = DesignConsts.ENABLE_DEBUG,
    isSum: Boolean = true, // by default SUM
    numberLength: Int = DesignConsts.NUMBER_LENGTH,
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

    val inputs = Input(Vec(countOfInputs, SInt(numberLength.W)))

    //
    // Output signals
    //
    val result = Output(SInt((2 * numberLength + log2Ceil(countOfInputs)).W))
    val resultValid = Output(Bool())
  })

  val resultOutput = WireInit(0.S((2 * numberLength + log2Ceil(countOfInputs)).W))

  var layerCompute = ReductionLayerCompute.Compute(debug)(countOfInputs)

  val delayCount = RegInit(0.S(layerCompute._2.W))
  val resultValid = WireInit(false.B)

  val regResultVec = Reg(
    Vec(
      layerCompute._1,
      new ResultOfMultiplicationAddition(2 * numberLength + log2Ceil(countOfInputs))
    )
  )

  //
  // Set output
  //
  resultOutput := regResultVec(layerCompute._1 - 2).multiplicationAdditionionResult

  LogInfo(debug)(
    "final multiplication/addition layer (result vector index): " + (layerCompute._1 - 2)
  )

  when(io.start) {

    //
    // Compute delay for validity of input
    //
    when(delayCount === layerCompute._2.S) {
      resultValid := true.B
    }.otherwise {
      delayCount := delayCount + 1.S
    }

    //
    // Implementation of the multiple multiplication/addition
    //
    var temp: Int = 0

    for (i <- 0 until layerCompute._1) {

      if ((layerCompute._3 == false && countOfInputs / 2 > i) || (layerCompute._3 == true && (countOfInputs - 1) / 2 > i)) {

        //
        // Connect inputs
        //
        //  regResultVec(i).multiplicationAdditionionResult := AdditionAndSubtraction(
        //    debug,
        //    isSum,
        //    numberLength
        //  )(
        //    io.start,
        //    io.inputs(i * 2),
        //    io.inputs(i * 2 + 1)
        //  )
        regResultVec(i).multiplicationAdditionionResult := MultiplicationAndDivision(
          debug,
          true, // it's MUL
          numberLength
        )(
          io.start,
          io.inputs(i * 2),
          io.inputs(i * 2 + 1)
        )

        LogInfo(debug)(
          "connecting inputs(" + (i * 2) + ") and input(" + (i * 2 + 1) + ") to regResultVec(" + i + ")"
        )

      } else {

        //
        // *** Connect multiplicators/adders ***
        //

        if (layerCompute._3 == true && i == layerCompute._1 - 1) {

          //
          // This is the last multiplicators/adders with odd inputs
          //
          regResultVec(i - 1).multiplicationAdditionionResult := AdditionAndSubtraction(
            debug,
            isSum,
            numberLength
          )(
            io.start,
            regResultVec(temp - 2).multiplicationAdditionionResult,
            io.inputs(countOfInputs - 1)
          )

          LogInfo(debug)(
            "connecting odd (exceptional) regResultVec(" + (temp - 2) + ") and inputs(" + (countOfInputs - 1) + ") to regResultVec(" + (i - 1) + ")"
          )

        } else if (i != temp + 1) {

          regResultVec(i).multiplicationAdditionionResult := AdditionAndSubtraction(
            debug,
            isSum,
            numberLength
          )(
            io.start,
            regResultVec(temp).multiplicationAdditionionResult,
            regResultVec(temp + 1).multiplicationAdditionionResult
          )

          LogInfo(debug)(
            "connecting regResultVec(" + temp + ") and regResultVec(" + (temp + 1) + ") to regResultVec(" + i + ")"
          )
        }

        temp += 2
      }

    }

  }.otherwise {

    //
    // Reset the multiple multiplicators/adders
    //
    delayCount := 0.S
    resultOutput := 0.S
    resultValid := false.B
  }

  //
  // Connect the outputs
  //
  io.result := resultOutput
  io.resultValid := resultValid

}

object MultipleMultiplicationAddition {

  def apply(
      debug: Boolean = DesignConsts.ENABLE_DEBUG,
      isSum: Boolean = true, // by default SUM
      numberLength: Int = DesignConsts.NUMBER_LENGTH,
      countOfInputs: Int = 0
  )(
      start: Bool,
      inputs: Vec[SInt]
  ): (SInt, Bool) = {

    val multipleMultiplicationAdditionModule = Module(
      new MultipleMultiplicationAddition(
        debug,
        isSum,
        numberLength,
        countOfInputs
      )
    )

    val result = Wire(SInt((2 * numberLength + log2Ceil(countOfInputs)).W))
    val resultValid = Wire(Bool())

    //
    // Configure the input signals
    //
    multipleMultiplicationAdditionModule.io.start := start
    multipleMultiplicationAdditionModule.io.inputs := inputs

    result := multipleMultiplicationAdditionModule.io.result
    resultValid := multipleMultiplicationAdditionModule.io.resultValid

    //
    // Return the output result
    //
    (result, resultValid)
  }
}
