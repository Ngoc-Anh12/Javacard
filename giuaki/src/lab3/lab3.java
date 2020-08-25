package lab3;

import javacard.framework.*;

public class lab3 extends Applet
{
	private byte[]buffer1,buffer2;
	private byte a_local;
	
	public static void install(byte[] bArray, short bOffset, byte bLength) 
	{
		new lab3().register(bArray, (short) (bOffset + 1), bArray[bOffset]);
	
	
	}

	public void process(APDU apdu)
	{
		if (selectingApplet())
		{
			return;
		}

		byte[] buf = apdu.getBuffer();
		apdu.setIncomingAndReceive();
		switch (buf[ISO7816.OFFSET_INS])
		{
		case (byte)0x00:
			a_local=1;
			buffer1 =new byte[5];
			buffer2=JCSystem.makeTransientByteArray((short)5,JCSystem.CLEAR_ON_RESET);	
					
			JCSystem.beginTransaction();
			for(short i=0;i<5;i++){
				buffer1[i]=(byte)(i+1);
				buffer2[i]=(byte)(2*(byte)(i+1));
			}
			buf[0]=a_local;
			JCSystem.abortTransaction();
			apdu.setOutgoing();
			apdu.setOutgoingLength((short)11);
		
			apdu.sendBytesLong(buffer1,(short)0,(short)5);
			apdu.sendBytesLong(buffer2,(short)0,(short)5);
			Util.arrayFillNonAtomic(buf,(short)0,(short)1,a_local);
			apdu.sendBytes((short)0,(short)1);
			break;
		default:
			ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
		}
	}

}
