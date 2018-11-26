


import java.io.*;
import java.util.*;

public class RiscV {

    static boolean count;
    static int pc;
    static int reg[] = new int[32];
    static byte mem[] = new byte[10000000];
    static ArrayList<Integer> progr=new ArrayList<>();

    static String binfiledestination = "C:\\Users\\Jakob\\Dropbox\\DTU\\5_Semester\\Computer_Arkitektur\\Final_Assignment\\Testing\\test_bgeu.bin";
    static String resfiledestination = "C:\\Users\\Jakob\\Dropbox\\DTU\\5_Semester\\Computer_Arkitektur\\Final_Assignment\\Testing\\test_bgeu.res";
    static String outputfiledestination = "C:\\Users\\Jakob\\Dropbox\\DTU\\5_Semester\\Computer_Arkitektur\\Final_Assignment\\output.res";


    public static void main(String[] args) throws IOException {
        System.out.println("Hello RISC-V World!");
        readBinFile(binfiledestination);
        pc = 0;
        reg[2] = 1000000;

        for (;;) {


            int instr = progr.get(pc/4);
            int opcode = instr & 0x7f;

            int funct3 = (instr >> 12) & 0x07;
            int funct7 = (instr >> 25);
            int rd = (instr >> 7) & 0x01f;
            int rs1 = (instr >> 15) & 0x01f;
            int rs2 = (instr >> 20) & 0x01f;
            int imm = 0;
            reg[0] = 0;

            count = true;

            switch (opcode) {


                case 0x37:    //LUI
                    imm = instr & 0xfffff000;
                    reg[rd] = imm;
                    break;

                case 0x17:    //AUIPC
                    imm = instr & 0xfffff000;
                    reg[rd] = imm + pc;
                    break;

                case 0x6f:    //JAL
                    imm = (((instr >> 21) & 0x3ff) << 1) + (((instr >> 20) & 0x1) << 11)
                            + (((instr >> 12) & 0xff) << 12) + (((instr >> 31) & 0x1) << 20) ;
                    if ((instr >>> 31) == 1) {
                        imm |= 0xfff00000;
                    }
                    reg[rd] = pc + 4;
                    pc += imm;
                    count = false;
                    break;

                case 0x67:    //JALR
                    imm = instr >> 20;
                    reg[rd] = pc + 4;
                    pc = (imm + reg[rs1]);
                    count = false;
                    break;


                case 0x63:   // B-type
                    imm = (((instr >> 8) & 0x0f) << 1) + (((instr >> 25) & 0x3f) << 5)
                            + (((instr >> 7) & 0x1) << 11) + (((instr >> 31) & 0x1) << 12);
                    if ((instr >>> 31) == 1) {
                        imm |= 0xffffe000;
                    }
                    //imm /= 4;
                    switch (funct3) {

                        case 0x00:             // BEQ
                            if (reg[rs1] == reg[rs2]) {
                                pc += imm;
                                count = false;
                            }
                            break;
                        case 0x01:             // BNE
                            if (reg[rs1] != reg[rs2]) {
                                pc += imm;
                                count = false;
                            }
                            break;
                        case 0x04:             // BLT
                            if (reg[rs1] < reg[rs2]) {
                                pc += imm;
                                count = false;
                            }
                            break;
                        case 0x05:             // BGE
                            if (reg[rs1] >= reg[rs2]) {
                                pc += imm;
                                count = false;
                            }
                            break;
                        case 0x06:              // BLTU
                            long a = reg[rs1];
                            long b = reg[rs2];
                            if ((a & 0xffffffffL) < (b & 0xffffffffL)) {
                                pc += imm;
                                count = false;
                            }
                            break;
                        case 0x07:              // BGTE
                            long aa = reg[rs1];
                            long bb = reg[rs2];
                            if ((aa & 0xffffffffL) >= (bb & 0xffffffffL)) {
                                pc += imm;
                                count = false;
                            }
                            break;
                    }
                    break;

                case 0x03:   //    load
                    imm = (instr >> 20);
                    if ((instr >>> 31) == 1) {
                        imm |= 0xfffff000;
                    }
                    switch (funct3) {
                        case 0x00:          // LB
                            reg[rd] = mem[imm + reg[rs1]];
                            break;

                        case 0x01:          // LH
                            int lh1 = mem[imm + reg[rs1]];
                            int lh2 =mem[imm + reg[rs1]+1];
                            reg[rd] = (lh1 & 0x000000ff) + ((lh2 << 8) & 0x0000ff00);

                            if ((mem[imm + reg[rs1]+1] < 0)) {
                                reg[rd] |= 0xffff0000;
                            }
                            break;

                        case 0x02:          // LW
                            int lw1 = mem[imm + reg[rs1]];
                            int lw2 = mem[imm + reg[rs1]+1];
                            int lw3 = mem[imm + reg[rs1]+2];
                            int lw4 = mem[imm + reg[rs1]+3];
                            reg[rd] = (lw1 & 0x000000ff) + ((lw2 << 8) & 0x0000ff00) + ((lw3 << 16) & 0x00ff0000) + ((lw4 << 24) & 0xff000000);
                            break;

                        case 0x04:          // LBU
                            int lbu1 = mem[imm + reg[rs1]];
                            reg[rd] = lbu1 & 0x000000ff;
                            break;

                        case 0x05:           // LHU
                            int lhu1 = mem[imm + reg[rs1]];
                            int lhu2 =mem[imm + reg[rs1]+1];
                            reg[rd] = (lhu1 & 0x000000ff) + ((lhu2 << 8) & 0x0000ff00);
                    }
                    break;

                case 0x23:   //    store
                    imm = ((instr >> 7) & 0x1f) + (((instr >> 25) & 0x7f) << 5);
                    if ((instr >>> 31) == 1) {
                        imm |= 0xfffff000;
                    }
                    switch (funct3) {

                        case 0x00:          // SB
                            mem[reg[rs1] + imm] = (byte) (reg[rs2] & 0xff);
                            break;

                        case 0x01:          // SH
                            mem[reg[rs1] + imm]   = (byte) (reg[rs2] & 0xff);
                            mem[reg[rs1] + imm+1] = (byte) ((reg[rs2] >>> 8) & 0xff);
                            break;

                        case 0x02:          // SW
                            mem[reg[rs1] + imm]   = (byte) (reg[rs2] & 0xff);
                            mem[reg[rs1] + imm+1] = (byte) ((reg[rs2] >>> 8) & 0xff);
                            mem[reg[rs1] + imm+2] = (byte) ((reg[rs2] >>> 16) & 0xff);
                            mem[reg[rs1] + imm+3] = (byte) ((reg[rs2] >>> 24) & 0xff);
                            break;
                    }
                    break;


                case 0x13:                    // I-type

                    imm = (instr >> 20);
                    switch (funct3) {

                        case 0x00:             // ADDI
                            reg[rd] = reg[rs1] + imm;
                            break;
                        case 0x02:             // SLTI
                            if (reg[rs1] < imm) {reg[rd]=1;} else {reg[rd]=0;};
                            break;
                        case 0x03:              // SLTIU
                            long sltiurs1 = reg[rs1];
                            long sltiuimm = imm;
                            if (sltiurs1 == 0) {
                                reg[rd] = 1;
                            }
                            else {
                                reg[rd] = 0;
                            }
                            if ((sltiurs1 & 0xffffffffL) < (sltiuimm & 0xffffffffL)) {reg[rd]=1;} else {reg[rd]=0;};
                            break;
                        case 0x04:             // XORI
                            reg[rd] = reg[rs1] ^ imm;
                            break;
                        case 0x06:             // ORI
                            reg[rd] = reg[rs1] | imm;
                            break;
                        case 0x07:             // ANDI
                            reg[rd] = reg[rs1] & imm;
                            break;
                        case 0x01:             // SLLI
                            reg[rd] = reg[rs1] << imm;
                            break;
                        case 0x05:
                            switch (funct7) {

                                case 0x00:         // SRLI
                                    reg[rd] = reg[rs1] >>> imm;
                                    break;
                                case 0x20:         // SRAI
                                    reg[rd] = reg[rs1] >> imm;
                                    break;
                            }
                            break;
                    }
                    break;
                case 0x33:
                    switch (funct3) {
                        case 0x00:
                            switch (funct7) {

                                case 0x00:         // ADD
                                    reg[rd] = reg[rs1] + reg[rs2];
                                    break;
                                case 0x20:         // SUB
                                    reg[rd] = reg[rs1] - reg[rs2];
                                    break;
                            }
                            break;

                        case 0x01:	           // SLL
                            reg[rd] = reg[rs1] << reg[rs2];
                            break;
                        case 0x02:	           // SLT
                            if (reg[rs1] < reg[rs2]) {reg[rd]=1;} else {reg[rd]=0;};
                            break;
                        case 0x03:	           // SLTU
                            long slturs1 = reg[rs1];
                            long slturs2 = reg[rs2];
                            if (slturs2 != 0) {
                                reg[rd] = 1;
                            }
                            else {
                                reg[rd] = 0;
                            }
                            if ((slturs1 & 0xffffffffL) < (slturs2 & 0xffffffffL)) {reg[rd]=1;} else {reg[rd]=0;};
                            break;

                        case 0x05:
                            switch (funct7) {

                                case 0x00:         // SRL
                                    reg[rd] = reg[rs1] >>> reg[rs2];
                                    break;
                                case 0x20:         // SRA
                                    reg[rd] = reg[rs1] >> reg[rs2];
                                    break;
                            }
                            break;

                        case 0x04:	           // XOR
                            reg[rd] = reg[rs1] ^ reg[rs2];
                            break;
                        case 0x06:	           // OR
                            reg[rd] = reg[rs1] | reg[rs2];
                            break;
                        case 0x7:	           // and
                            reg[rd] = reg[rs1] & reg[rs2];
                            break;
                    }
                    break;


                case 0x73:    //ECALL
                    pc = 4 * progr.size();
                    break;

                default:
                    System.out.println("Opcode " + opcode + " not yet implemented");
                    break;
            }

            if (count == true) {
                pc += 4; // We count in 4 byte words
            }

            if (pc / 4 >= progr.size()) {
                break;
            }

            for (int i = 0; i < reg.length; ++i) {
                System.out.print(reg[i] + " ");
            }
            System.out.println(pc);

        }
        writeOutputFile(); //Turn on to write output file
        System.out.println("Program exit");
        checkOutputfile(outputfiledestination, resfiledestination);
    }


    public static void readBinFile(String filedestination) throws IOException{
        DataInputStream binfile = new DataInputStream(new FileInputStream(filedestination));
        while(binfile.available() > 0) {
            try
            {
                int instruction = binfile.readInt();
                instruction = Integer.reverseBytes(instruction);
                progr.add(instruction);
            }
            catch(EOFException e) {
                //just catches the error.. only in loop.bin
            }
        }
        binfile.close();
    }
    public static void writeOutputFile() throws IOException {
        DataOutputStream outputFile = new DataOutputStream(new FileOutputStream("output.res"));
        byte[] regBytes;
        for (int i = 0; i < reg.length; ++i) {
            regBytes = intToBytes(Integer.reverseBytes(reg[i]));
            for(int j = 0; j<4; j++) {
                outputFile.write(regBytes[j]);
            }
        }
        outputFile.flush();
        outputFile.close();
    }
    public static byte[] intToBytes(int number) {
        byte[] conversion = new byte[4];
        conversion[0] = (byte) (number >>> 24);
        conversion[1] = (byte) (number >>> 16);
        conversion[2] = (byte) (number >>> 8);
        conversion[3] = (byte) (number);
        return conversion;
    }

    public static void checkOutputfile(String outputdest, String resdest)throws IOException {
        ArrayList<Integer> outputarr=new ArrayList<>();
        ArrayList<Integer> resarr=new ArrayList<>();
        DataInputStream outputfile = new DataInputStream(new FileInputStream(outputdest));
        DataInputStream resfile = new DataInputStream(new FileInputStream(resdest));
        while(outputfile.available() > 0) {
            try
            {
                int output = outputfile.readInt();
                output = Integer.reverseBytes(output);
                outputarr.add(output);
            }
            catch(EOFException e) {
                //just catches the error.. only in loop.bin
            }
        }
        outputfile.close();
        while(resfile.available() > 0) {
            try
            {
                int res = resfile.readInt();
                res = Integer.reverseBytes(res);
                resarr.add(res);
            }
            catch(EOFException e) {
                //just catches the error.. only in loop.bin
            }
        }
        resfile.close();

        outputarr.set(2,0);
        boolean err = false;
        if(outputarr.equals(resarr)){
            System.out.println("Correct");
        }
        else
            System.out.println("ERROR");
    }

}