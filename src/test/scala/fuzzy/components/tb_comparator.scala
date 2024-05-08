package fuzzy

import chisel3._
import chisel3.experimental.BundleLiterals._
import chisel3.simulator.EphemeralSimulator._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

import fuzzy.components._
import fuzzy.utils._

class ComparatorTest extends AnyFreeSpec with Matchers {
  "DUT should pass" in {

    simulate(
      new Comparator(
        DesignConsts.ENABLE_DEBUG,
        true,
        true, // Least index first
        TestingSample.comparator_test_len
      )
    ) { dut =>
      //
      // First, start with module in an inactive state
      //
      dut.io.start.poke(false.B)
      dut.clock.step(1)

      //
      // Perform the first test
      //
      val test1 =
        Integer.parseInt(TestingSample.comparator_test1.replace(" ", ""), 2).U
      val test2 =
        Integer.parseInt(TestingSample.comparator_test2.replace(" ", ""), 2).U

      //
      // Start the comparator
      //
      dut.io.start.poke(true.B)

      dut.io.in1.poke(test1)
      dut.io.in2.poke(test2)

      dut.clock.step(1)

      //
      // Test the results of the above module
      //
      if (
        test1.litValue.toInt > test2.litValue.toInt &&
        dut.io.maxMin.peek().litValue.toInt == 0
      ) {
        print(
          "\n[*] Test result for regular comparator (max) was successful.\n"
        );
      } else if (
        test1.litValue.toInt < test2.litValue.toInt &&
        dut.io.maxMin.peek().litValue.toInt == 1
      ) {
        print(
          "\n[*] Test result for regular comparator (max) was successful.\n"
        );
      } else {
        print(
          "\n[x] Test result for regular comparator (max) was NOT successful!\n"
        );
        assert(false, "Err, test failed")
      }

      //
      // Remove the start bit again
      //
      dut.io.start.poke(false.B)
    }
  }
  simulate(
    new Comparator(
      DesignConsts.ENABLE_DEBUG,
      false,
      true, // Least index first
      TestingSample.comparator_test_len
    )
  ) { dut =>
    //
    // First, start with module in an inactive state
    //
    dut.io.start.poke(false.B)
    dut.clock.step(1)

    //
    // Perform the first test
    //
    val test1 =
      Integer.parseInt(TestingSample.comparator_test1.replace(" ", ""), 2).U
    val test2 =
      Integer.parseInt(TestingSample.comparator_test2.replace(" ", ""), 2).U

    //
    // Start the comparator
    //
    dut.io.start.poke(true.B)

    dut.io.in1.poke(test1)
    dut.io.in2.poke(test2)

    dut.clock.step(1)

    //
    // Test the results of the above module
    //
    if (
      test1.litValue.toInt < test2.litValue.toInt &&
      dut.io.maxMin.peek().litValue.toInt == 0
    ) {
      print(
        "\n[*] Test result for regular comparator (min) was successful.\n"
      );

    } else if (
      test1.litValue.toInt > test2.litValue.toInt &&
      dut.io.maxMin.peek().litValue.toInt == 1
    ) {
      print(
        "\n[*] Test result for regular comparator (min) was successful.\n"
      );
    } else {
      print(
        "\n[x] Test result for regular comparator (min) was NOT successful!\n"
      );
      assert(false, "Err, test failed")
    }

    //
    // Remove the start bit again
    //
    dut.io.start.poke(false.B)
  }
}
