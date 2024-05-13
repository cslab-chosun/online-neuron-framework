package neuron.components

import chisel3._
import chisel3.util._

// _root_ disambiguates from package chisel3.util.circt if user imports chisel3.util._
import _root_.circt.stage.ChiselStage

import neuron.utils._

class FullAdder extends Module {
  val io = IO(new Bundle {
    val x = Input(Bool())
    val y = Input(Bool())
    val z = Input(Bool())
    val s = Output(Bool())
    val c = Output(Bool())
  })

  io.s := io.x ^ io.y ^ io.z
  io.c := (io.x & io.y) | (io.x & io.z) | (io.z & io.y)
}

class OLSDA(
    debug: Boolean = DesignConsts.ENABLE_DEBUG
) extends Module {

  val io = IO(new Bundle {
    val x = Input(UInt(4.W))
    val cin = Input(Bool())
    val z = Output(UInt(2.W))
    val cout = Output(Bool())
  })

  val FA1 = Module(new FullAdder)
  val FA2 = Module(new FullAdder)

  val R1 = RegInit(false.B)
  val R2 = RegInit(false.B)
  val R3 = RegInit(false.B)
  val R4 = RegInit(false.B)
  val R5 = RegInit(false.B)
  val R6 = RegInit(false.B)

  FA1.io.x := io.x(3)
  FA1.io.y := io.x(2)
  FA1.io.z := io.x(1)
  val sFA1 = FA1.io.s
  val cout = FA1.io.c

  FA2.io.x := R1
  FA2.io.y := R2
  FA2.io.z := io.cin
  val sFA2 = FA2.io.s
  val cFA2 = FA2.io.c

  R1 := sFA1
  R2 := io.x(0)
  R3 := sFA2
  R4 := cFA2
  R6 := R4
  R5 := R3

  io.z := Cat(R6, R5)
  io.cout := cout
}

object OLSDA {

  def apply(
      debug: Boolean = DesignConsts.ENABLE_DEBUG
  )(
      x: UInt,
      cin: Bool
  ): (UInt, Bool) = {

    val oLSDAModule = Module(
      new OLSDA(
        debug
      )
    )

    val z = Wire(UInt(2.W))
    val cout = Wire(Bool())

    //
    // Configure the input signals
    //
    oLSDAModule.io.x := x
    oLSDAModule.io.cin := cin

    z := oLSDAModule.io.z
    cout := oLSDAModule.io.cout

    //
    // Return the output result
    //
    (z, cout)
  }
}

object MainOnlineAdderGenerator extends App {

  //
  // Generate verilog files
  //
  println(
    ChiselStage.emitSystemVerilog(
      new OLSDA(
        true // debug
      ),
      firtoolOpts = Array(
        "-disable-all-randomization",
        "-strip-debug-info",
        "--split-verilog", // The intention for this argument (and next argument) is to separate generated files.
        "-o",
        "generated/online/adder/"
      )
    )
  )
}
