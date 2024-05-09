package neuron

import chisel3._
import chisel3.experimental.BundleLiterals._
import chisel3.simulator.EphemeralSimulator._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

import neuron.components._
import neuron.utils._

class MultipleComparatorTest extends AnyFreeSpec with Matchers {
  "DUT should pass" in {

    simulate(
      new MultipleComparator(
        true, // DesignConsts.ENABLE_DEBUG
        true, // it's max
        false, // index based
        true, // least index first
        8, // legnth of each input number
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
      dut.io.inputs(0).poke(0xaa.U)
      dut.io.inputs(1).poke(0x21.U)
      dut.io.inputs(2).poke(0x77.U)
      dut.io.inputs(3).poke(0x55.U)
      dut.io.inputs(4).poke(0x81.U)
      dut.io.inputs(5).poke(0x22.U)
      dut.io.inputs(6).poke(0x33.U)
      dut.io.inputs(7).poke(0x44.U)
      dut.io.inputs(8).poke(0xa7.U)

      //
      // Step the inputs
      //
      dut.clock.step(10)

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
      dut.io.inputs(0).poke(0x50.U)
      dut.io.inputs(1).poke(0x26.U)
      dut.io.inputs(2).poke(0x12.U)
      dut.io.inputs(3).poke(0x0.U)
      dut.io.inputs(4).poke(0x3.U)
      dut.io.inputs(5).poke(0x1f.U)
      dut.io.inputs(6).poke(0x27.U)
      dut.io.inputs(7).poke(0x31.U)
      dut.io.inputs(8).poke(0x09.U)

      //
      // Step the inputs
      //
      dut.clock.step(10)

      // ---------------------------------------------------------------

      //
      // Remove the start bit again
      //
      dut.io.start.poke(false.B)
    }
  }
}
