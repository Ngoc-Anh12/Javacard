package project;

import javacard.framework.*;

public class project extends Applet
{
//khai bao lenh
private static final byte ex_add = (byte) 0x01;
private static final byte ex_mul = (byte) 0x02;
	public static void install(byte[] bArray, short bOffset, byte bLength) 
	{
		new project().register(bArray, (short) (bOffset + 1), bArray[bOffset]);
	}

	public void process(APDU apdu)
	{
		if (selectingApplet())
		{
			return;
		}

		byte[] buf = apdu.getBuffer();
		apdu.setIncomingAndReceive();
		short i=0;
		short tmp1=buf[ISO7816.OFFSET_P1];
		short tmp2=buf[ISO7816.OFFSET_P2];
		switch (buf[ISO7816.OFFSET_INS])
		{
		case ex_add:
			i=(short)(tmp1+tmp2);
			Util.setShort(buf,(short)0,i);
			apdu.setOutgoingAndSend((short)0,(short)2);
			break;
		case ex_mul:
			if(tmp1<tmp2){
				ISOException.throwIt(ISO7816.SW_INCORRECT_P1P2);
				
			}
			else{
				i=(short)(tmp1-tmp2);
				Util.setShort(buf,(short)0,i);
				apdu.setOutgoingAndSend((short)0,(short)2);
				
			}
			break;
		default:
			ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
		}
	}

}
