import random

import cocotb
from cocotb.clock import Clock
from cocotb.triggers import Timer
from cocotb.types import LogicArray

'''
  input         clock,
                reset,
                io_start,
  input  [31:0] io_inputs_0,
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
  output [31:0] io_result
'''

@cocotb.test()
async def ComparatorBasedRegularNeuronComputation_test(dut):
    """Test Regular Neuron Computation Based on Comparators"""

    #
    # Assert initial output is unknown
    #
    assert LogicArray(dut.io_result.value) == LogicArray("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX")

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
    
    dut.io_inputs_0.value = 0
    dut.io_inputs_1.value = 1
    dut.io_inputs_2.value = 2
    dut.io_inputs_3.value = 3
    dut.io_inputs_4.value = 4
    dut.io_inputs_5.value = 5
    dut.io_inputs_6.value = 6
    dut.io_inputs_7.value = 7
    dut.io_inputs_8.value = 8
    dut.io_inputs_9.value = 9
    dut.io_inputs_10.value = 10
    dut.io_inputs_11.value = 11
    dut.io_inputs_12.value = 12
    dut.io_inputs_13.value = 13
    dut.io_inputs_14.value = 14
    dut.io_inputs_15.value = 15
    dut.io_inputs_16.value = 16
    dut.io_inputs_17.value = 17
    dut.io_inputs_18.value = 18
    dut.io_inputs_19.value = 19
    dut.io_inputs_20.value = 20
    dut.io_inputs_21.value = 21
    dut.io_inputs_22.value = 22
    dut.io_inputs_23.value = 23
    dut.io_inputs_24.value = 24
    dut.io_inputs_25.value = 25
    dut.io_inputs_26.value = 26
    dut.io_inputs_27.value = 27
    dut.io_inputs_28.value = 28
    dut.io_inputs_29.value = 29
    dut.io_inputs_30.value = 30
    dut.io_inputs_31.value = 31
    dut.io_inputs_32.value = 32
    dut.io_inputs_33.value = 33
    dut.io_inputs_34.value = 34
    dut.io_inputs_35.value = 35
    dut.io_inputs_36.value = 36
    dut.io_inputs_37.value = 37
    dut.io_inputs_38.value = 38
    dut.io_inputs_39.value = 39
    dut.io_inputs_40.value = 40
    dut.io_inputs_41.value = 41
    dut.io_inputs_42.value = 42
    dut.io_inputs_43.value = 43
    dut.io_inputs_44.value = 44
    dut.io_inputs_45.value = 45
    dut.io_inputs_46.value = 46
    dut.io_inputs_47.value = 47
    dut.io_inputs_48.value = 48
    dut.io_inputs_49.value = 49
    dut.io_inputs_50.value = 50
    dut.io_inputs_51.value = 51
    dut.io_inputs_52.value = 52
    dut.io_inputs_53.value = 53
    dut.io_inputs_54.value = 54
    dut.io_inputs_55.value = 55
    dut.io_inputs_56.value = 56
    dut.io_inputs_57.value = 57
    dut.io_inputs_58.value = 58
    dut.io_inputs_59.value = 59
    dut.io_inputs_60.value = 60
    dut.io_inputs_61.value = 61
    dut.io_inputs_62.value = 62
    dut.io_inputs_63.value = 63


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
