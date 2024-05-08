import random

import cocotb
from cocotb.clock import Clock
from cocotb.triggers import Timer
from cocotb.types import LogicArray

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

@cocotb.test()
async def OnlineNeuronComputation_test(dut):
    """Test Online Neuron Computation"""

    #
    # Assert initial output is unknown
    #
    assert LogicArray(dut.io_result.value) == LogicArray("X")
    assert LogicArray(dut.io_resultValid.value) == LogicArray("X")

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
    
    dut.io_inputs_0.value = 1
    dut.io_inputs_1.value = 1
    dut.io_inputs_2.value = 1
    dut.io_inputs_3.value = 1
    dut.io_inputs_4.value = 1
    dut.io_inputs_5.value = 1
    dut.io_inputs_6.value = 1
    dut.io_inputs_7.value = 1
    dut.io_inputs_8.value = 1
    dut.io_inputs_9.value = 0
    dut.io_inputs_10.value = 1
    dut.io_inputs_11.value = 1
    dut.io_inputs_12.value = 1
    dut.io_inputs_13.value = 1
    dut.io_inputs_14.value = 1
    dut.io_inputs_15.value = 0
    dut.io_inputs_16.value = 1
    dut.io_inputs_17.value = 1
    dut.io_inputs_18.value = 1
    dut.io_inputs_19.value = 1
    dut.io_inputs_20.value = 0
    dut.io_inputs_21.value = 1
    dut.io_inputs_22.value = 1
    dut.io_inputs_23.value = 1
    dut.io_inputs_24.value = 0
    dut.io_inputs_25.value = 0
    dut.io_inputs_26.value = 0
    dut.io_inputs_27.value = 1
    dut.io_inputs_28.value = 1
    dut.io_inputs_29.value = 1
    dut.io_inputs_30.value = 1
    dut.io_inputs_31.value = 0
    dut.io_inputs_32.value = 0
    dut.io_inputs_33.value = 0
    dut.io_inputs_34.value = 1
    dut.io_inputs_35.value = 1
    dut.io_inputs_36.value = 0
    dut.io_inputs_37.value = 0
    dut.io_inputs_38.value = 0
    dut.io_inputs_39.value = 0
    dut.io_inputs_40.value = 0
    dut.io_inputs_41.value = 0
    dut.io_inputs_42.value = 0
    dut.io_inputs_43.value = 1
    dut.io_inputs_44.value = 1
    dut.io_inputs_45.value = 1
    dut.io_inputs_46.value = 1
    dut.io_inputs_47.value = 0
    dut.io_inputs_48.value = 1
    dut.io_inputs_49.value = 1
    dut.io_inputs_50.value = 1
    dut.io_inputs_51.value = 0
    dut.io_inputs_52.value = 0
    dut.io_inputs_53.value = 0
    dut.io_inputs_54.value = 0
    dut.io_inputs_55.value = 0
    dut.io_inputs_56.value = 1
    dut.io_inputs_57.value = 1
    dut.io_inputs_58.value = 1
    dut.io_inputs_59.value = 1
    dut.io_inputs_60.value = 0
    dut.io_inputs_61.value = 1
    dut.io_inputs_62.value = 1
    dut.io_inputs_63.value = 1

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
