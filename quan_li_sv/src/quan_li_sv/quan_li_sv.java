package quan_li_sv;

import javacard.framework.*;

public class quan_li_sv extends Applet
{
	private static final byte INS_INFO=(byte)0x02;
	private static final byte INS_PRINT=(byte)0x03;
	private static byte[]id,username,dob;
	private static short[]t;
	short data_length,username_length,id_length,dob_length;
	short i,j;
	public static void install(byte[] bArray, short bOffset, byte bLength) 
	{
		new quan_li_sv().register(bArray, (short) (bOffset + 1), bArray[bOffset]);
		
			id = new byte[5];
			username=new byte [50];
			dob=new byte[5];
			t=new short[5];
	
	
	}

	public void process(APDU apdu)
	{
		if (selectingApplet())
		{
			return;
		}
		apdu.setIncomingAndReceive();
		
		byte[] buf = apdu.getBuffer();
		data_length=(short)(buf[ISO7816.OFFSET_LC]&0xff);
		switch (buf[ISO7816.OFFSET_INS])
		{
		case INS_INFO:
			j=(short)0;
			short index=(short)(ISO7816.OFFSET_CDATA);
			for(i=index;i<(short)(ISO7816.OFFSET_CDATA+1+data_length);i++){
				if(buf[i]==(byte)0x60){
					t[j]=i;
					j++;
				}
				
			}
			username_length=(short)(t[0]-index);
			id_length=(short)(t[1]-t[0]-1);
			dob_length=(short)(index+data_length-t[1]-1);
			j=(short)0;
			for(i=index;i<t[0];i++){
				username[j]=buf[i];
				j++;
			}
			j=(short)0;
			for(i=(short)(t[0]+1);i<t[1];i++){
				dob[j]=buf[i];
				j++;
			}
			break;
		case INS_PRINT:
			short total_len=(short)(username_length+id_length+dob_length);
			short length=apdu.setOutgoing();
			apdu.setOutgoingLength(total_len);
			apdu.sendBytesLong(username,(short)0,username_length);
			apdu.sendBytesLong(id,(short)0,id_length);
			apdu.sendBytesLong(dob,(short)0,dob_length);
			break;
		default:
			ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
		}
	}

}
