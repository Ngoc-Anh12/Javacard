package lab4_lan2;

import javacard.framework.*;

public class lab4_lan2 extends Applet
{
	private static final byte INS_MSV=(byte)0x00;
	private static final byte INS_NHAP=(byte)0x01;
	private static byte[]sinhvien;
	final static byte sv_id_len=(byte)0x03;
	private static byte somon;
	private static byte[]diemthi;
	short index,i,datalen,len;
	public static void install(byte[] bArray, short bOffset, byte bLength) 
	{
		new lab4_lan2(bArray,bOffset, bLength);
	}
	
	private lab4_lan2(byte[] bArray, short bOffset, byte bLength){
		byte bLilen=bArray[bOffset];
			if(bLilen==0){
				register();
			}
			else{
				register(bArray,(short)(bOffset+1),bLilen);
				
			}
			bOffset=(short)(bOffset+bLilen+1);
			byte bLclen=bArray[bOffset];
			bOffset=(short)(bOffset+bLclen+1);
			byte bLalen=bArray[bOffset];
			bOffset=(short)(bOffset+1);
			if(bLalen!=0){
				sinhvien=new byte[sv_id_len];
				Util.arrayCopy(bArray,bOffset,sinhvien,(byte)0,sv_id_len);
				bOffset+=sv_id_len;
				somon=bArray[bOffset];
				
			}
			else{
				sinhvien=new byte[]{'A','N','H'};
				somon=(byte)0x05;
				
			}
		len=(short)(2*(short)somon);
		diemthi=new byte[len];
		
		
	}
	public void process(APDU apdu)
	{
		if (selectingApplet())
		{
			return;
		}

		byte[] buf = apdu.getBuffer();
		apdu.setIncomingAndReceive();
		datalen=(short)(buf[ISO7816.OFFSET_LC]);
		switch (buf[ISO7816.OFFSET_INS])
		{
		case INS_MSV:
			apdu.setOutgoing();
			apdu.setOutgoingLength((short)1);
			apdu.sendBytesLong(sinhvien,(short)0,sv_id_len);
			buf[0]=somon;
			apdu.sendBytes((short)0,(short)1);
			break;
		case INS_NHAP:
			short j=0;
			index=(short)(buf[ISO7816.OFFSET_CDATA]);
			//kiem tra nhap qua so mon
			if(datalen<=len){
				short k= 0;
				short x=0;
				for(i=index;i<(short)(index+(short)(datalen-2));i+=2){
					for(k=(short)(index+2);k<(short)(index+(short)(datalen-1));k+=2){
						if(buf[i]==buf[k]){
							x=1;
							break;
						}
					}
				}
				if(x==0){
					for(i=index;i<(short)(index+datalen);i++){
						diemthi[j]=buf[i];
						j++;
					}
				}
				else{
					ISOException.throwIt(ISO7816.SW_WRONG_DATA);
				}
			}
			
			else{
				ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
			}
		apdu.setOutgoing();
		apdu.setOutgoingLength(len);
		apdu.sendBytesLong(diemthi,(short)0,len);
			break;
			
		default:
			ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
		}
	}

}
