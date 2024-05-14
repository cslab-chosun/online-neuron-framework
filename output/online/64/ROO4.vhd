library ieee; 
    use ieee.std_logic_1164.all;
    use ieee.numeric_std.all; 

entity ROO4 is
    generic 
    (
        width   : integer 
    );
    port 
    (
        clk, rst        :   in  std_logic;
        xp, xn          :   in  std_logic; 
        yp, yn          :   in  std_logic;
        zp, zn          :   out std_logic;
        sum1            :   out integer range -4 to 4;
        sum2            :   out integer range -4 to 4;
        sum_total       :   out integer range -4 to 4
    );
end entity;

architecture arch of ROO4 is 

    -- Delay for inputs 
    signal xp_reg               : std_logic;
    signal xn_reg               : std_logic;
    signal yp_reg               : std_logic;
    signal yn_reg               : std_logic;

    -- Conversion/Appending Signals 
    signal Xp_value_reg         : std_logic_vector(width - 1 downto 0);
    signal Xp_value_next        : std_logic_vector(width - 1 downto 0);
    signal Xp_value_masked      : std_logic_vector(width - 1 downto 0);
    
    signal Xn_value_reg         : std_logic_vector(width - 1 downto 0);
    signal Xn_value_next        : std_logic_vector(width - 1 downto 0);
    signal Xn_value_masked      : std_logic_vector(width - 1 downto 0);
    
    signal Yp_value_reg         : std_logic_vector(width - 1 downto 0);
    signal Yp_value_next        : std_logic_vector(width - 1 downto 0);
    signal Yp_value_masked      : std_logic_vector(width - 1 downto 0);
        
    signal Yn_value_reg         : std_logic_vector(width - 1 downto 0);
    signal Yn_value_next        : std_logic_vector(width - 1 downto 0);
    signal Yn_value_masked      : std_logic_vector(width - 1 downto 0);

    -- Partial Product Output Signals 
    signal Xp_selected       : std_logic_vector(width - 1 downto 0);
    signal Yp_selected       : std_logic_vector(width - 1 downto 0);
    
    signal Xn_selected       : std_logic_vector(width - 1 downto 0);
    signal Yn_selected       : std_logic_vector(width - 1 downto 0);


    -- Mask for Appending inputs to the X and Y Registers
    signal mask             : std_logic_vector(width downto 0);
    signal mask_not         : std_logic_vector(width downto 0);
   -- constant mask_init_val  : std_logic_vector(width downto 0) := (width => '1', others => '0');


    
    -- Resuial Values Signals 
    signal WS2_reg           : std_logic_vector(width  downto 0);
    signal WC2_reg           : std_logic_vector(width  downto 0);

    -- Partial Values   
    signal PS1               : std_logic_vector(width  downto 0);
    signal PC1               : std_logic_vector(width  downto 0);
    signal PS2               : std_logic_vector(width  downto 0);
    signal PC2               : std_logic_vector(width  downto 0);


    signal adder_4_2_2_din2 : std_logic_vector(width   downto 0);
    signal adder_4_2_2_din3 : std_logic_vector(width   downto 0);




    signal adder_4_2_1_cout   : std_logic_vector(1 downto 0);
    signal adder_4_2_1_cin    : std_logic_vector(1 downto 0);
    signal adder_4_2_2_cout   : std_logic_vector(1 downto 0);
    signal adder_4_2_2_cin    : std_logic_vector(1 downto 0);



    signal FAp              : std_logic;
    signal FAn              : std_logic;
    signal FTp              : std_logic;
    signal FTn              : std_logic;
    signal WS_msb           : std_logic;
    signal WC_msb           : std_logic;

    signal sum1_sig             : integer range -4 to 4;
    signal sum2_sig             : integer range -4 to 4;
    signal sum_total_sig        : integer range -4 to 4;

    ----------
    -- State signals
    ----------
    signal state      : integer range -2 to 2;
    signal next_state : integer range -2 to 2;

    signal FSM_dout             : integer range -1 to 1;




    function extend(x : std_logic; n : integer) return std_logic_vector is
        variable return_value   : std_logic_vector(n - 1 downto 0);
    begin 
        for i in 0 to n - 1 loop
            return_value(i) := x;
        end loop;

        return return_value;
    end function;


    component adder_3_2 is 
        generic 
        (
            width   : integer := 8
        );
        port 
        (
            din0    : in    std_logic_vector(width-1 downto 0);
            din1    : in    std_logic_vector(width-1 downto 0);
            din2    : in    std_logic_vector(width-1 downto 0);
            cin     : in    std_logic;
            dout0   : out   std_logic_vector(width-1 downto 0);
            dout1   : out   std_logic_vector(width-1 downto 0);
            cout    : out   std_logic
        );
    end component;


    component adder_4_2 is 
        generic 
        (
            width   : integer := 8
        );
        port 
        (
            din0    : in    std_logic_vector(width-1 downto 0);
            din1    : in    std_logic_vector(width-1 downto 0);
            din2    : in    std_logic_vector(width-1 downto 0);
            din3    : in    std_logic_vector(width-1 downto 0);
            cin     : in    std_logic_vector(1 downto 0);
            dout0   : out   std_logic_vector(width-1 downto 0);
            dout1   : out   std_logic_vector(width-1 downto 0);
            cout    : out   std_logic_vector(1 downto 0)
        );
    end component;

    component ROO3_FSM is 
        port 
        (
            clk, rst    :   in  std_logic;
            din         :   in  integer range -2 to 2;
            dout        :   out integer range -1 to 1
        );
    end component;


begin 

    -- 
    -- Delay Register for inputs 
    --
    process(clk)
    begin 
        if rising_edge(clk) then 
            if rst = '1' then 
                xp_reg <= '0'; 
                xn_reg <= '1'; 
                yp_reg <= '0'; 
                yn_reg <= '1'; 
            else 
                xp_reg <= xp;
                xn_reg <= xn;
                yp_reg <= yp;
                yn_reg <= yn;
            end if;
        end if;
    end process;


    --
    -- MSBF Conversion/Appending 
    --
    process (clk)
    begin 
        if rising_edge(clk) then 
            if rst = '1' then 
                Xp_value_reg <= (others => '0');
                Yp_value_reg <= (others => '0');
                Xn_value_reg <= (others => '1');
                Yn_value_reg <= (others => '1');
                mask(width) <= '1';
                mask(width-1 downto 0) <= (others => '0');
            else 

                Xp_value_reg <= Xp_value_next;
                Yp_value_reg <= Yp_value_next;
                
                Xn_value_reg <= Xn_value_next;
                Yn_value_reg <= Yn_value_next;

                mask <= '0' & mask(width downto 1);
            end if;
        end if;
    end process;

    Xp_value_masked <= mask(width - 1 downto 0) when xp_reg = '1' else
                        (others => '0');
    Xp_value_next <= Xp_value_masked or Xp_value_reg; 


    Xn_value_masked <= mask_not(width - 1 downto 0) when xn_reg = '0' else
                        (others => '1');
    Xn_value_next <= Xn_value_masked and Xn_value_reg;     


    Yp_value_masked <= mask(width downto 1) when yp = '1' else
                        (others => '0');
    Yp_value_next <= Yp_value_masked or Yp_value_reg;   


    Yn_value_masked <= mask_not(width downto 1) when yn = '0' else
                        (others => '1');
    Yn_value_next <= Yn_value_masked and Yn_value_reg;  

    mask_not <= not mask;



    Xp_selected <= (Xp_value_reg and extend(yp_reg, width)) or
                    ((not extend(yn_reg, width)) and (not Xn_value_reg));

    Xn_selected <= ((not Xp_value_reg) and (not extend(yp_reg, width))) or
                    (Xn_value_reg and extend(yn_reg, width));


    Yp_selected <= (Yp_value_reg and extend(xp_reg, width)) or
                    ((not extend(xn_reg, width)) and (not Yn_value_reg));

    Yn_selected <= ((not Yp_value_reg) and (not extend(xp_reg, width))) or
                    (Yn_value_reg and extend(xn_reg, width));



    
    adder_4_2_1_cin <= "10";
    adder_4_2_1_i: adder_4_2 
        generic map
        (
            width
        )
        port map
        (
            din0 => Xp_selected,    
            din1 => Xn_selected,    
            din2 => Yp_selected,    
            din3 => Yn_selected,    
            cin =>  adder_4_2_1_cin,    
            dout0 => PS1(width-1 downto 0),  
            dout1 => PC1(width-1 downto 0), 
            cout => adder_4_2_1_cout
        );

    PS1(width) <= adder_4_2_1_cout(0);
    PC1(width) <= adder_4_2_1_cout(1);
    --PS1(width+1) <= '0';
    --PC1(width+1) <= '1';




    adder_4_2_2_din2 <= WS2_reg(width -1 downto 0) & '0';      
    adder_4_2_2_din3 <= WC2_reg(width -1 downto 0) & '1';           
            
    adder_4_2_2_cin <= "10";
    adder_4_2_2_i: adder_4_2 
        generic map
        (
            width + 1 
        )
        port map
        (
            din0 => PS1,    
            din1 => PC1,    
            din2 => adder_4_2_2_din2,    
            din3 => adder_4_2_2_din3,    
            cin =>  adder_4_2_2_cin,    
            dout0 => PS2(width DOWNTO 0),  
            dout1 => PC2(width DOWNTO 0), 
            cout => adder_4_2_2_cout
        );
    
    --PS2(width+1) <= adder_4_2_2_cout(0);
    --PC2(width+1) <= adder_4_2_2_cout(1);
    -- 
    -- Residual Values Registers 
    --
    process (clk)
    begin 
        if rising_edge(clk) then 
            if rst = '1' then 
                WS2_reg <= (others => '0');
                WC2_reg <= (others => '1');
            else 
                WS2_reg <= PS2;
                WC2_reg <= PC2;
            end if;
        end if;
    end process;


    --
    -- Calculate sum of signals 
    --
    FAp <= '0';
    FAn <= '1';
    FTp <= adder_4_2_2_cout(0); --WS2_reg(width+1); -- 
    FTn <= adder_4_2_2_cout(1); --WC2_reg(width+1); -- 
    WS_msb <= WS2_reg(width);
    ---WS_msb <= PS2(width);
    WC_msb <= WC2_reg(width);
    --- WC_msb <= PC2(width);
    process 
    (
        FAp,
        FAn,
        FTp,    
        FTn,   
        WS_msb,  
        WC_msb 
    )
        variable sum1       : integer range -4 to 4;
        variable sum2       : integer range -4 to 4;
        variable sum_total  : integer range -4 to 4;
    begin 
        sum1 := 0;
        sum2 := 0;
        if FAp = '1' then
            sum1 := sum1 + 1;
        end if; 

        if FAn = '0' then 
            sum1 := sum1 - 1; 
        end if; 

        if FTp = '1' then
            sum1 := sum1 + 1;
        end if; 

        if FTn = '0' then 
            sum1 := sum1 - 1; 
        end if; 

        if WS_msb = '1' then
            sum2 := sum2 + 1;
        end if; 

        if WC_msb = '0' then 
            sum2 := sum2 - 1; 
        end if; 

        sum1_sig <= sum1;
        sum2_sig <= sum2;
        sum_total := sum1 + sum2;
        sum_total_sig <= sum1 + sum2;
    end process; 

    sum1 <= sum1_sig;     
    sum2 <= sum2_sig;
    sum_total <= sum_total_sig;   

 
    ROO3_FSM_i: ROO3_FSM
    port map
    (
        clk, 
        rst, 
        sum_total_sig,                
        FSM_dout
    );

    zp <= '1' when FSM_dout = 1 else
          '0';

    zn <= '0' when FSM_dout = -1 else 
          '1';   
    
    
end arch;
