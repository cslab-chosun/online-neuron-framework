// Generated by CIRCT firtool-1.62.0
module ROO4_Verilog(	// src/main/scala/neuron/components/online_multiplier.scala:61:7
  input  clock,	// src/main/scala/neuron/components/online_multiplier.scala:61:7
         reset,	// src/main/scala/neuron/components/online_multiplier.scala:61:7
         io_xp,	// src/main/scala/neuron/components/online_multiplier.scala:62:14
         io_xn,	// src/main/scala/neuron/components/online_multiplier.scala:62:14
         io_yp,	// src/main/scala/neuron/components/online_multiplier.scala:62:14
         io_yn,	// src/main/scala/neuron/components/online_multiplier.scala:62:14
  output io_zp,	// src/main/scala/neuron/components/online_multiplier.scala:62:14
         io_zn	// src/main/scala/neuron/components/online_multiplier.scala:62:14
);

  ROO4_Blackbox roo4 (	// src/main/scala/neuron/components/online_multiplier.scala:74:20
    .clock     (clock),
    .reset     (reset),
    .xp        (io_xp),
    .xn        (io_xn),
    .yp        (io_yp),
    .yn        (io_yn),
    .zp        (io_zp),
    .zn        (io_zn),
    .sum1      (/* unused */),
    .sum2      (/* unused */),
    .sum_total (/* unused */)
  );
endmodule

