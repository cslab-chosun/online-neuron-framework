package neuron

import chisel3._
import chisel3.experimental.BundleLiterals._
import chisel3.simulator.EphemeralSimulator._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

import neuron.components._
import neuron.utils._

class OnlineMultipleComparatorTest extends AnyFreeSpec with Matchers {
  "DUT should pass" in {

    simulate(
      new OnlineMultipleComparator(
        true, // DesignConsts.ENABLE_DEBUG
        true, // it's a MAX comparator
        true, // index based
        false, // least index first
        9, // number of inputs in vector
        50 // This shows the maximum number that is possible for an index to be
      )
    ) { dut =>
      // ---------------------------------------------------------------

      //
      // First, start with module in an inactive state
      //
      dut.io.start.poke(false.B)
      dut.clock.step(1)

      //
      // Activate the input
      //
      dut.io.start.poke(true.B)

      //
      // Add the input vector
      //
      val testCase0 = 0xaa.U(8.W)
      val testCase1 = 0x21.U(8.W)
      val testCase2 = 0x77.U(8.W)
      val testCase3 = 0x55.U(8.W)
      val testCase4 = 0x81.U(8.W)
      val testCase5 = 0x22.U(8.W)
      val testCase6 = 0x33.U(8.W)
      val testCase7 = 0x44.U(8.W)
      val testCase8 = 0xa7.U(8.W)

      for (i <- 0 until 8) {

        dut.io.inputs(0).poke(testCase0(8 - i - 1))
        dut.io.inputs(1).poke(testCase1(8 - i - 1))
        dut.io.inputs(2).poke(testCase2(8 - i - 1))
        dut.io.inputs(3).poke(testCase3(8 - i - 1))
        dut.io.inputs(4).poke(testCase4(8 - i - 1))
        dut.io.inputs(5).poke(testCase5(8 - i - 1))
        dut.io.inputs(6).poke(testCase6(8 - i - 1))
        dut.io.inputs(7).poke(testCase7(8 - i - 1))
        dut.io.inputs(8).poke(testCase8(8 - i - 1))

        dut.clock.step(1)

      }

      //
      // Step the inputs
      //
      dut.clock.step(20)

      // ---------------------------------------------------------------

      //
      // Remove the start bit again
      //
      dut.io.start.poke(false.B)
    }
  }
}
