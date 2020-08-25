package lab4;

import javacard.framework.*;

public class lab4 extends Applet
{
	private static final byte INS_MSV =(byte)0x00;
	private static final byte INS_NHAP =(byte)0x01;
	private static final byte INS_IN =(byte)0x02;
	private static final byte INS_SUA =(byte)0x03;
	private static final byte INS_XOA =(byte)0x04;
	
	private byte sv_id_len=(byte) 0x07;
	private static byte[]diem,sinhvien;
	private static byte somon;
	private short datalen,len,index,i,j;
	public static void install(byte[] bArray, short bOffset, byte bLength) 
	{
		new lab4(bArray,bOffset,bLength);
	}
	private lab4(byte[] bArray, short bOffset, byte bLength){
		byte bLi=bArray[bOffset];//do dai AID
		
		if(bLi==0){
			register();//dang ki applet voi JCRE voi AID mac dinh
		}
		else{
			register(bArray,(short)(bOffset+1),bLi);
			
		}
		bOffset=(short)(bOffset+bLi+1);//byte cua AID
		byte bLc=bArray[bOffset];
		bOffset=(short)(bOffset+bLc+1);
		byte bLa=bArray[bOffset];
		bOffset=(short)(bOffset+1);
		//doc du lieu cua applet
		if(bLa!=0){
			//gan id sinh vien
				sinhvien=new byte[sv_id_len];
				Util.arrayCopy(bArray,bOffset,sinhvien,(byte)0,sv_id_len);
				bOffset+=sv_id_len;
				//gan so mon thi
				somon=bArray[bOffset];
		}
		else{
			sinhvien=new byte[]{'N','G','O','C','A','N','H'};
			somon=(byte)0x04;
		}
		len=(short)(2*(short)(somon));
		diem=new byte[len];
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
			apdu.setOutgoingLength((short)8);
			apdu.sendBytesLong(sinhvien,(short)0,sv_id_len);
			buf[0]=somon;
			apdu.sendBytes((short)0,(short)1);
			break;
		case INS_NHAP:
			//kiem tra nhap khong qua so mon
			index=(short)(buf[ISO7816.OFFSET_CDATA]);
			if(datalen<=len){
				short k=0,x=0;
				for(i=index;i<(short)(index+(short)(datalen-2));i+=2){
					for(k=(short)(i+2);k<(short)(index+(short)(datalen-1));k+=2){
						if(buf[i]==buf[k]){
							x=1;
							break;
						}
						
					}
				}
				if(x==0){
					for(i=index;i<(short)(index+datalen);i++){
						diem[j]=buf[i];
						j++;
					}
				}
			}
			apdu.setOutgoing();
			apdu.setOutgoingLength(len);
			apdu.sendBytesLong(diem,(short)0,len);
			
			break;
		case INS_IN:
			break;
		case INS_SUA:
			break;
		case INS_XOA:
			break;
		default:
			ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
		}
	}

}
