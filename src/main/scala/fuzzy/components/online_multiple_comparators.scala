package fuzzy.components

import chisel3._
import chisel3.util._

import fuzzy.components._
import fuzzy.utils._

class ResultOfOnlineMinOrMax(
    isIndexBased: Boolean = false, // by should we return index of maximum value element or the value
    maximumNumberOfIndex: Int = DesignConsts.NUMBER_LENGTH
) extends Bundle {

  val minMaxResult = UInt(1.W)
  val earlyTerminate = UInt(1.W)
  val minMaxIndex = UInt(log2Ceil(maximumNumberOfIndex).W)

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

  val maxMinOutput = WireInit(0.U(1.W))
  val maxMinIndexOutput = WireInit(0.U(log2Ceil(maximumNumberOfIndex).W))

  var layerCompute = LayerCompute.Compute(debug)(countOfInputs)

  val delayCount = RegInit(0.U(layerCompute._2.W))
  val resultValid = WireInit(false.B)

  val regMinMaxResultVec = Reg(
    Vec(
      layerCompute._1,
      new ResultOfOnlineMinOrMax(isIndexBased, maximumNumberOfIndex)
    )
  )

  if (!isIndexBased) {
    maxMinOutput := regMinMaxResultVec(layerCompute._1 - 2).minMaxResult

  } else {
    maxMinIndexOutput := regMinMaxResultVec(layerCompute._1 - 2).minMaxIndex
    maxMinOutput := regMinMaxResultVec(layerCompute._1 - 2).minMaxResult
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
        regMinMaxResultVec(i).minMaxResult := resultMaxMinOutput
        var resultEarlyTerminate = resultEarlyTerminate1 | resultEarlyTerminate2
        regMinMaxResultVec(i).earlyTerminate := resultEarlyTerminate

        if (isIndexBased) {

          when(
            resultEarlyTerminate2 === true.B && resultEarlyTerminate === true.B
          ) {
            regMinMaxResultVec(i).minMaxIndex := (i * 2).U;
          }.elsewhen(
            resultEarlyTerminate1 === true.B && resultEarlyTerminate === true.B
          ) {
            regMinMaxResultVec(i).minMaxIndex := (i * 2 + 1).U;
          }
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
            regMinMaxResultVec(temp - 2).minMaxResult,
            io.inputs(countOfInputs - 1),
            false.B
          )

          //
          // Connect the outputs
          //
          regMinMaxResultVec(i - 1).minMaxResult := resultMaxMinOutput
          var resultEarlyTerminate =
            resultEarlyTerminate1 | resultEarlyTerminate2
          regMinMaxResultVec(i - 1).earlyTerminate := resultEarlyTerminate

          if (isIndexBased) {
            when(
              resultEarlyTerminate2 === true.B && resultEarlyTerminate === true.B
            ) {
              regMinMaxResultVec(i - 1).minMaxIndex := regMinMaxResultVec(
                temp - 2
              ).minMaxIndex;
            }.elsewhen(
              resultEarlyTerminate1 === true.B && resultEarlyTerminate === true.B
            ) {
              regMinMaxResultVec(i - 1).minMaxIndex := (countOfInputs - 1).U;
            }
          }

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
            regMinMaxResultVec(temp).minMaxResult,
            regMinMaxResultVec(temp + 1).minMaxResult,
            false.B
          )

          //
          // Connect the outputs
          //
          regMinMaxResultVec(i).minMaxResult := resultMaxMinOutput
          var resultEarlyTerminate =
            resultEarlyTerminate1 | resultEarlyTerminate2
          regMinMaxResultVec(i).earlyTerminate := resultEarlyTerminate

          if (isIndexBased) {
            when(
              resultEarlyTerminate2 === true.B && resultEarlyTerminate === true.B
            ) {
              regMinMaxResultVec(i).minMaxIndex := regMinMaxResultVec(
                temp
              ).minMaxIndex;
            }.elsewhen(
              resultEarlyTerminate1 === true.B && resultEarlyTerminate === true.B
            ) {
              regMinMaxResultVec(i).minMaxIndex := regMinMaxResultVec(
                temp + 1
              ).minMaxIndex;
            }
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
