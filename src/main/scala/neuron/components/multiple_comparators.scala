package neuron.components

import chisel3._
import chisel3.util._

import neuron.utils._

class ResultOfMinOrMax(
    isIndexBased: Boolean = false, // by should we return index of maximum value element or the value
    numberLength: Int = DesignConsts.NUMBER_LENGTH,
    maximumNumberOfIndex: Int = DesignConsts.NUMBER_LENGTH
) extends Bundle {

  val minMaxResult = UInt(numberLength.W)
  val minMaxIndex = UInt(log2Ceil(maximumNumberOfIndex).W)

}

object LayerCompute {

  def Compute(debug: Boolean = DesignConsts.ENABLE_DEBUG)(
      countOfInputs: Int
  ): (Int, Int, Boolean) = {

    //
    // Check if number of inputs is odd or event
    //
    var isOddNumberOfInputs: Boolean = false
    var tempRemainedInputCount: Int = countOfInputs
    var tempComparatorCount: Int = 0
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
        tempComparatorCount += 1
        delayCycles += 1
      }

      tempRemainedInputCount = tempRemainedInputCount / 2
      tempComparatorCount += tempRemainedInputCount
      delayCycles += 1
    }

    //
    // if it's odd then we should consider another comparison too
    //
    if (isOddNumberOfInputs) {
      tempComparatorCount = tempComparatorCount + 1
      delayCycles = delayCycles + 1
    }
    LogInfo(debug)("multiple comparator | count of inputs: " + countOfInputs)
    LogInfo(debug)(
      "multiple comparator | number of needed comparators: " + tempComparatorCount
    )
    LogInfo(debug)("multiple comparator | delay cycles: " + delayCycles)

    //
    // Return the number of needed comparators and delays
    //
    (tempComparatorCount + 1, delayCycles, isOddNumberOfInputs)
  }
}

class MultipleComparator(
    debug: Boolean = DesignConsts.ENABLE_DEBUG,
    isMax: Boolean = true, // by default MAX Comparator
    isIndexBased: Boolean = false, // by should we return index of maximum value element or the value
    leastIndexFirst: Boolean = true, // by deafult least index is shown first (in case of equal operands)
    numberLength: Int = DesignConsts.NUMBER_LENGTH,
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

    val inputs = Input(Vec(countOfInputs, UInt(numberLength.W)))

    //
    // Output signals
    //
    val result = Output(UInt(numberLength.W))
    val resultValid = Output(Bool())
  })

  val maxMinOutput = WireInit(0.U(numberLength.W))

  var layerCompute = LayerCompute.Compute(debug)(countOfInputs)

  val delayCount = RegInit(0.U(layerCompute._2.W))
  val resultValid = WireInit(false.B)

  val regMinMaxResultVec = Reg(
    Vec(
      layerCompute._1,
      new ResultOfMinOrMax(isIndexBased, numberLength, maximumNumberOfIndex)
    )
  )

  if (!isIndexBased) {
    maxMinOutput := regMinMaxResultVec(layerCompute._1 - 2).minMaxResult
  } else {
    maxMinOutput := regMinMaxResultVec(layerCompute._1 - 2).minMaxIndex
  }

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
        if (!isIndexBased) {
          regMinMaxResultVec(i).minMaxResult := Comparator(
            false /*debug*/,
            isMax,
            leastIndexFirst,
            numberLength
          )(
            io.start,
            io.inputs(i * 2),
            io.inputs(i * 2 + 1)
          )
        } else {

          val comparatorOutput = Comparator.apply2(
            false /*debug*/,
            isMax,
            leastIndexFirst,
            numberLength
          )(
            io.start,
            io.inputs(i * 2),
            io.inputs(i * 2 + 1),
            (i * 2).U,
            (i * 2 + 1).U
          )
          regMinMaxResultVec(i).minMaxResult := comparatorOutput._1
          regMinMaxResultVec(i).minMaxIndex := comparatorOutput._2
        }

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
          if (!isIndexBased) {

            regMinMaxResultVec(i - 1).minMaxResult := Comparator(
              false /*debug*/,
              isMax,
              leastIndexFirst,
              numberLength
            )(
              io.start,
              regMinMaxResultVec(temp - 2).minMaxResult,
              io.inputs(countOfInputs - 1)
            )
          } else {

            val comparatorOutput = Comparator.apply2(
              false /*debug*/,
              isMax,
              leastIndexFirst,
              numberLength
            )(
              io.start,
              regMinMaxResultVec(temp - 2).minMaxResult,
              io.inputs(countOfInputs - 1),
              regMinMaxResultVec(temp - 2).minMaxIndex,
              (countOfInputs - 1).U
            )

            regMinMaxResultVec(i - 1).minMaxResult := comparatorOutput._1
            regMinMaxResultVec(i - 1).minMaxIndex := comparatorOutput._2

          }

          LogInfo(debug)(
            "connecting odd (exceptional) regMinMaxResultVec(" + (temp - 2) + ") and inputs(" + (countOfInputs - 1) + ") to regMinMaxResultVec(" + (i - 1) + ")"
          )

        } else if (i != temp + 1) {

          if (!isIndexBased) {
            regMinMaxResultVec(i).minMaxResult := Comparator(
              false /*debug*/,
              isMax,
              leastIndexFirst,
              numberLength
            )(
              io.start,
              regMinMaxResultVec(temp).minMaxResult,
              regMinMaxResultVec(temp + 1).minMaxResult
            )
          } else {

            val comparatorOutput = Comparator.apply2(
              false /*debug*/,
              isMax,
              leastIndexFirst,
              numberLength
            )(
              io.start,
              regMinMaxResultVec(temp).minMaxResult,
              regMinMaxResultVec(temp + 1).minMaxResult,
              regMinMaxResultVec(temp).minMaxIndex,
              regMinMaxResultVec(temp + 1).minMaxIndex
            )

            regMinMaxResultVec(i).minMaxResult := comparatorOutput._1
            regMinMaxResultVec(i).minMaxIndex := comparatorOutput._2

          }

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
    resultValid := false.B
  }

  //
  // Connect the outputs
  //
  io.result := maxMinOutput
  io.resultValid := resultValid

}

object MultipleComparator {

  def apply(
      debug: Boolean = DesignConsts.ENABLE_DEBUG,
      isMax: Boolean = true, // by default MAX Comparator
      isIndexBased: Boolean = false, // by should we return index of maximum value element or the value
      leastIndexFirst: Boolean = true, // by deafult least index is shown first (in case of equal operands)
      numberLength: Int = DesignConsts.NUMBER_LENGTH,
      countOfInputs: Int = 0,
      maximumNumberOfIndex: Int = 10 // in case if isIndexBased == TRUE
      // (This shows the maximum number that is possible for an index to be)
  )(
      start: Bool,
      inputs: Vec[UInt]
  ): UInt = {

    val comparatorModule = Module(
      new MultipleComparator(
        debug,
        isMax,
        isIndexBased,
        leastIndexFirst,
        numberLength,
        countOfInputs,
        maximumNumberOfIndex
      )
    )

    val result = Wire(UInt(numberLength.W))

    //
    // Configure the input signals
    //
    comparatorModule.io.start := start
    comparatorModule.io.inputs := inputs

    result := comparatorModule.io.result

    //
    // Return the output result
    //
    result
  }
}
