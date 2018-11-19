
import java.io.*;
import java.util.*;

public class RiscV {

	static int pc;
	static int reg[] = new int[32];
	static ArrayList<Integer> progr=new ArrayList<>();
	
	public static void main(String[] args) throws IOException {
		System.out.println("Hello RISC-V World!");
		readBinFile("C:/Users/Jakob/Desktop/addlarge.bin");
		pc = 0;

        for (;;) {

            int instr = progr.get(pc);
            int opcode = instr & 0x7f;

            int funct3 = (instr >> 12) & 0x07;
            int funct7 = (instr >> 25);
            int rd = (instr >> 7) & 0x01f;
            int rs1 = (instr >> 15) & 0x01f;
            int rs2 = (instr >> 20) & 0x01f;
            int imm = 0;

            switch (opcode) {

                case 0x37:    //LUI
                    imm = instr & 0xfffff000;
                    reg[rd] = imm;
                    break;

                case 0x63:   // B-type
                    imm = (((instr >>> 8) & 0x0f) << 1) + (((instr >>> 25) & 0x3f) << 5)
                            + (((instr >>> 7) & 0x1) << 11) + (((instr >>> 31) & 0x1) << 12) ;
                    if ((instr >>> 31) == 1) {
                        imm |= 0xffffe000;
                    }

                    switch (funct3) {

                        case 0x00:             // BEQ
                            if (reg[rs1] == reg[rs2]) {pc += imm;}
                            break;
                        case 0x01:             // BNE
                            if (reg[rs1] != reg[rs2]) {pc += imm;}
                            break;
                        case 0x04:             // BLT
                            if (reg[rs1] < reg[rs2]) {pc += imm;}
                            break;
                        case 0x05:             // BGE
                            if (reg[rs1] >= reg[rs2]) {pc += imm;}
                            break;
                    }
                    break;

                case 0x13:
                    imm = (instr >>> 20);
                    switch (funct3) {

                        case 0x00:             // ADDI
                            reg[rd] = reg[rs1] + imm;
                            break;
                        case 0x02:             // SLTI
                            if (reg[rs1] < imm) {reg[rd]=1;} else {reg[rd]=0;};
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
                        case 0x05:             // SRLI
                            reg[rd] = reg[rs1] << imm;
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
                        case 0x05:	           // SRL
                            reg[rd] = reg[rs1] >> reg[rs2];
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
                    pc = progr.size();
                    break;


                default:
                    System.out.println("Opcode " + opcode + " not yet implemented");
                    break;
            }


            ++pc; // We count in 4 byte words

            if (pc >= progr.size()) {
                break;
            }

            for (int i = 0; i < reg.length; ++i) {
                System.out.print(reg[i] + " ");
            }
            System.out.println();
        }
        writeOutputFile();
        System.out.println("Program exit");
    }


	public static void readBinFile(String filedestination) throws IOException{
		DataInputStream binfile = new DataInputStream(new FileInputStream(filedestination));
		while(binfile.available() > 0) {
			int instruction = binfile.readInt();
			instruction = Integer.reverseBytes(instruction);
			progr.add(instruction);
		}
		binfile.close();
	}
	public static void writeOutputFile() throws IOException {
        DataOutputStream outputFile = new DataOutputStream(new FileOutputStream("output.bin"));
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

}