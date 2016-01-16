package interpreterImpl.chip8;

public abstract class Chip8FamilyInterpreter {
	protected int opcode;

	public abstract void operation0();

	public abstract void operation1();

	public abstract void operation2();

	public abstract void operation3();

	public abstract void operation4();

	public abstract void operation5();

	public abstract void operation6();

	public abstract void operation7();

	public abstract void operation8();

	public abstract void operation9();

	public abstract void operationA();

	public abstract void operationB();

	public abstract void operationC();

	public abstract void operationD();

	public abstract void operationE();

	public abstract void operationF() throws InterruptedException;

	public void executeOpcode() throws Exception {
		System.out.printf("%X\n", opcode & 0xF000);
		switch (opcode & 0xF000) {
		case 0x0000:
			operation0();
			break;
		case 0x1000:
			operation1();
			break;
		case 0x2000:
			operation2();
			break;
		case 0x3000:
			operation3();
			break;
		case 0x4000:
			operation4();
			break;
		case 0x5000:
			operation5();
			break;
		case 0x6000:
			operation6();
			break;
		case 0x7000:
			operation7();
			break;
		case 0x8000:
			operation8();
			break;
		case 0x9000:
			operation9();
			break;
		case 0xA000:
			operationA();
			break;
		case 0xB000:
			operationB();
			break;
		case 0xC000:
			operationC();
			break;
		case 0xD000:
			operationD();
			break;
		case 0xE000:
			operationE();
			break;
		case 0xF000:
			operationF();
			break;
		}
	}
}
