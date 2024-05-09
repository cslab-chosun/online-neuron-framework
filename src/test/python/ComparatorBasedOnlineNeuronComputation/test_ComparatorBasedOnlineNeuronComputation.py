import random

import cocotb
from cocotb.clock import Clock
from cocotb.triggers import Timer
from cocotb.types import LogicArray

number_of_bits = 64

'''
  input  clock,
         reset,
         io_start,
         io_inputs_0,
         io_inputs_1,
         io_inputs_2,
         io_inputs_3,
         io_inputs_4,
         io_inputs_5,
         io_inputs_6,
         io_inputs_7,
         io_inputs_8,
         io_inputs_9,
         io_inputs_10,
         io_inputs_11,
         io_inputs_12,
         io_inputs_13,
         io_inputs_14,
         io_inputs_15,
         io_inputs_16,
         io_inputs_17,
         io_inputs_18,
         io_inputs_19,
         io_inputs_20,
         io_inputs_21,
         io_inputs_22,
         io_inputs_23,
         io_inputs_24,
         io_inputs_25,
         io_inputs_26,
         io_inputs_27,
         io_inputs_28,
         io_inputs_29,
         io_inputs_30,
         io_inputs_31,
         io_inputs_32,
         io_inputs_33,
         io_inputs_34,
         io_inputs_35,
         io_inputs_36,
         io_inputs_37,
         io_inputs_38,
         io_inputs_39,
         io_inputs_40,
         io_inputs_41,
         io_inputs_42,
         io_inputs_43,
         io_inputs_44,
         io_inputs_45,
         io_inputs_46,
         io_inputs_47,
         io_inputs_48,
         io_inputs_49,
         io_inputs_50,
         io_inputs_51,
         io_inputs_52,
         io_inputs_53,
         io_inputs_54,
         io_inputs_55,
         io_inputs_56,
         io_inputs_57,
         io_inputs_58,
         io_inputs_59,
         io_inputs_60,
         io_inputs_61,
         io_inputs_62,
         io_inputs_63,
  output io_result,
         io_resultValid
'''

def get_nth_bit(number, n):
    """
    Returns the nth bit of the given number.

    Parameters:
        number (int): The number whose nth bit is to be retrieved.
        n (int): The position of the bit to retrieve (0-indexed from the right).

    Returns:
        int: The value of the nth bit (either 0 or 1).
    """
    return (number >> n) & 1

@cocotb.test()
async def ComparatorBasedOnlineNeuronComputation_test(dut):
    """Test Online Neuron Computation Based on Comparators"""

    global number_of_bits

    #
    # Add initial values
    #
    dut.io_inputs_0.value =  0
    dut.io_inputs_1.value =  0
    dut.io_inputs_2.value =  0
    dut.io_inputs_3.value =  0
    dut.io_inputs_4.value =  0
    dut.io_inputs_5.value =  0
    dut.io_inputs_6.value =  0
    dut.io_inputs_7.value =  0
    dut.io_inputs_8.value =  0
    dut.io_inputs_9.value =  0
    dut.io_inputs_10.value = 0
    dut.io_inputs_11.value = 0
    dut.io_inputs_12.value = 0
    dut.io_inputs_13.value = 0
    dut.io_inputs_14.value = 0
    dut.io_inputs_15.value = 0
    dut.io_inputs_16.value = 0
    dut.io_inputs_17.value = 0
    dut.io_inputs_18.value = 0
    dut.io_inputs_19.value = 0
    dut.io_inputs_20.value = 0
    dut.io_inputs_21.value = 0
    dut.io_inputs_22.value = 0
    dut.io_inputs_23.value = 0
    dut.io_inputs_24.value = 0
    dut.io_inputs_25.value = 0
    dut.io_inputs_26.value = 0
    dut.io_inputs_27.value = 0
    dut.io_inputs_28.value = 0
    dut.io_inputs_29.value = 0
    dut.io_inputs_30.value = 0
    dut.io_inputs_31.value = 0
    dut.io_inputs_32.value = 0
    dut.io_inputs_33.value = 0
    dut.io_inputs_34.value = 0
    dut.io_inputs_35.value = 0
    dut.io_inputs_36.value = 0
    dut.io_inputs_37.value = 0
    dut.io_inputs_38.value = 0
    dut.io_inputs_39.value = 0
    dut.io_inputs_40.value = 0
    dut.io_inputs_41.value = 0
    dut.io_inputs_42.value = 0
    dut.io_inputs_43.value = 0
    dut.io_inputs_44.value = 0
    dut.io_inputs_45.value = 0
    dut.io_inputs_46.value = 0
    dut.io_inputs_47.value = 0
    dut.io_inputs_48.value = 0
    dut.io_inputs_49.value = 0
    dut.io_inputs_50.value = 0
    dut.io_inputs_51.value = 0
    dut.io_inputs_52.value = 0
    dut.io_inputs_53.value = 0
    dut.io_inputs_54.value = 0
    dut.io_inputs_55.value = 0
    dut.io_inputs_56.value = 0
    dut.io_inputs_57.value = 0
    dut.io_inputs_58.value = 0
    dut.io_inputs_59.value = 0
    dut.io_inputs_60.value = 0
    dut.io_inputs_61.value = 0
    dut.io_inputs_62.value = 0
    dut.io_inputs_63.value = 0
    
    #
    # Assert initial output is unknown
    #
    # assert LogicArray(dut.io_result.value) == LogicArray("Z")
    # assert LogicArray(dut.io_resultValid.value) == LogicArray("Z")

    #
    # Create a 10ns period clock on port clock
    #
    clock = Clock(dut.clock, 10, units="ns")
    
    #
    # Start the clock. Start it low to avoid issues on the first RisingEdge
    #
    cocotb.start_soon(clock.start(start_high=False))
    
    dut._log.info("Initialize and reset module")

    #
    # Initial values
    #
    dut.io_start.value = 0

    #
    # Reset DUT
    #
    dut.reset.value = 1
    for _ in range(50):
        await Timer(10, units="ns")
    dut.reset.value = 0

    dut._log.info("Enabling chip")

    #
    # Enable chip
    #
    dut.io_start.value = 1

    #
    # Set initial input value to prevent it from floating
    #
    for i in range(number_of_bits):
      dut.io_inputs_0.value =  get_nth_bit(0x5555555555555555, number_of_bits - i)
      dut.io_inputs_1.value =  get_nth_bit(0x12, number_of_bits - i)
      dut.io_inputs_2.value =  get_nth_bit(0x124, number_of_bits - i)
      dut.io_inputs_3.value =  get_nth_bit(0x453fa, number_of_bits - i)
      dut.io_inputs_4.value =  get_nth_bit(0x3423, number_of_bits - i)
      dut.io_inputs_5.value =  get_nth_bit(0x543554345, number_of_bits - i)
      dut.io_inputs_6.value =  get_nth_bit(0xabc78650128, number_of_bits - i)
      dut.io_inputs_7.value =  get_nth_bit(0x23544365fa, number_of_bits - i)
      dut.io_inputs_8.value =  get_nth_bit(0xfafafafaf, number_of_bits - i)
      dut.io_inputs_9.value =  get_nth_bit(0x434532423, number_of_bits - i)
      dut.io_inputs_10.value = get_nth_bit(0x56324534, number_of_bits - i)
      dut.io_inputs_11.value = get_nth_bit(0x21421421, number_of_bits - i)
      dut.io_inputs_12.value = get_nth_bit(0x43634, number_of_bits - i)
      dut.io_inputs_13.value = get_nth_bit(0x3467889, number_of_bits - i)
      dut.io_inputs_14.value = get_nth_bit(0x32523325ab, number_of_bits - i)
      dut.io_inputs_15.value = get_nth_bit(0x34534ab, number_of_bits - i)
      dut.io_inputs_16.value = get_nth_bit(0x32423ab, number_of_bits - i)
      dut.io_inputs_17.value = get_nth_bit(0xba35235, number_of_bits - i)
      dut.io_inputs_18.value = get_nth_bit(0x5325623, number_of_bits - i)
      dut.io_inputs_19.value = get_nth_bit(0x7457645, number_of_bits - i)
      dut.io_inputs_20.value = get_nth_bit(0x122346, number_of_bits - i)
      dut.io_inputs_21.value = get_nth_bit(0x122346, number_of_bits - i)
      dut.io_inputs_22.value = get_nth_bit(0xab523252, number_of_bits - i)
      dut.io_inputs_23.value = get_nth_bit(0x52362fa, number_of_bits - i)
      dut.io_inputs_24.value = get_nth_bit(0x6343fa, number_of_bits - i)
      dut.io_inputs_25.value = get_nth_bit(0x965645bc, number_of_bits - i)
      dut.io_inputs_26.value = get_nth_bit(0xbbc214312, number_of_bits - i)
      dut.io_inputs_27.value = get_nth_bit(0x43534523, number_of_bits - i)
      dut.io_inputs_28.value = get_nth_bit(0xbb23423, number_of_bits - i)
      dut.io_inputs_29.value = get_nth_bit(0x6456645, number_of_bits - i)
      dut.io_inputs_30.value = get_nth_bit(0xda54, number_of_bits - i)
      dut.io_inputs_31.value = get_nth_bit(0x0, number_of_bits - i)
      dut.io_inputs_32.value = get_nth_bit(0x32, number_of_bits - i)
      dut.io_inputs_33.value = get_nth_bit(0x46547, number_of_bits - i)
      dut.io_inputs_34.value = get_nth_bit(0x124345231fa, number_of_bits - i)
      dut.io_inputs_35.value = get_nth_bit(0x45343fac, number_of_bits - i)
      dut.io_inputs_36.value = get_nth_bit(0x23423facc, number_of_bits - i)
      dut.io_inputs_37.value = get_nth_bit(0xcaf342432, number_of_bits - i)
      dut.io_inputs_38.value = get_nth_bit(0x71278123a, number_of_bits - i)
      dut.io_inputs_39.value = get_nth_bit(0xabcbbcaba, number_of_bits - i)
      dut.io_inputs_40.value = get_nth_bit(0x234532, number_of_bits - i)
      dut.io_inputs_41.value = get_nth_bit(0x986759, number_of_bits - i)
      dut.io_inputs_42.value = get_nth_bit(0x23423, number_of_bits - i)
      dut.io_inputs_43.value = get_nth_bit(0xab, number_of_bits - i)
      dut.io_inputs_44.value = get_nth_bit(0x0, number_of_bits - i)
      dut.io_inputs_45.value = get_nth_bit(0xfafa, number_of_bits - i)
      dut.io_inputs_46.value = get_nth_bit(0x354bc, number_of_bits - i)
      dut.io_inputs_47.value = get_nth_bit(0x4532ba63, number_of_bits - i)
      dut.io_inputs_48.value = get_nth_bit(0xab21431, number_of_bits - i)
      dut.io_inputs_49.value = get_nth_bit(0x23432, number_of_bits - i)
      dut.io_inputs_50.value = get_nth_bit(0x12412, number_of_bits - i)
      dut.io_inputs_51.value = get_nth_bit(0x1134523, number_of_bits - i)
      dut.io_inputs_52.value = get_nth_bit(0x146234b, number_of_bits - i)
      dut.io_inputs_53.value = get_nth_bit(0x890, number_of_bits - i)
      dut.io_inputs_54.value = get_nth_bit(0x2222, number_of_bits - i)
      dut.io_inputs_55.value = get_nth_bit(0x2124312, number_of_bits - i)
      dut.io_inputs_56.value = get_nth_bit(0x235532325352, number_of_bits - i)
      dut.io_inputs_57.value = get_nth_bit(0x346, number_of_bits - i)
      dut.io_inputs_58.value = get_nth_bit(0x23423, number_of_bits - i)
      dut.io_inputs_59.value = get_nth_bit(0xaabc, number_of_bits - i)
      dut.io_inputs_60.value = get_nth_bit(0x23423, number_of_bits - i)
      dut.io_inputs_61.value = get_nth_bit(0x32432, number_of_bits - i)
      dut.io_inputs_62.value = get_nth_bit(0xfa3, number_of_bits - i)
      dut.io_inputs_63.value = get_nth_bit(0xfae231, number_of_bits - i)

      #
      # Run for one clock-cycle
      #
      await Timer(10, units="ns")

    #
    # Synchronize with the clock. This will regisiter the initial `inputs_X` value
    #
    await Timer(10, units="ns")
    
    #
    # Check the final input on the next clock and run the circuit for a couple
    # of more clock cycles
    #
    for _ in range(1000):
        await Timer(10, units="ns")
