package lab4_giuaki;

import javacard.framework.*;

public class lab4_giuaki extends Applet
{
	private static final byte INS_MSV=(byte)0x00;
	private static final byte INS_NHAP=(byte)0x01;
	private static final byte INS_IN=(byte)0x02;
	private static final byte INS_SUA=(byte)0x03;
	private static final byte INS_XOA=(byte)0x04;
	private static byte[]diemthi,sinhvien;
	final static byte sv_id_len=(byte)0x03;
	private static byte somon,id,diem;
	short datalen,len,index,i,diemlen;
	public static void install(byte[] bArray, short bOffset, byte bLength) 
	{
		new lab4_giuaki(bArray,bOffset, bLength);
	}
	
	private lab4_giuaki(byte[] bArray, short bOffset, byte bLength){
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
			len=(short)(2*(somon));
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
		id=(byte)(buf[ISO7816.OFFSET_P1]);
		diem=(byte)(buf[ISO7816.OFFSET_P2]);
		switch (buf[ISO7816.OFFSET_INS])
		{
		case INS_MSV:
			apdu.setOutgoing();
			apdu.setOutgoingLength((short)3);
			apdu.sendBytesLong(sinhvien,(short)0,sv_id_len);
			
			break;
		case INS_NHAP:
			short j=0;
			index=(short)(ISO7816.OFFSET_CDATA);
			if(datalen<=len){
				short k=0;
				short x=0;
				for(i=index;i<(short)(index+(short)(datalen-3));i+=2){
					for(k=(short)(i+2);k<(short)(index+(short)(datalen-1));k+=2){
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
					diemlen=j;
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
		case INS_IN:
			apdu.setOutgoing();
			apdu.setOutgoingLength((short)10);
			apdu.sendBytesLong(diemthi,(short)0,(short)10);
			break;
		case INS_SUA:
			index =(short)-1;
			for(i=0;i<(short)(diemlen-1);i+=2){
				if(id==diemthi[i]){
					index=i;
					break;
				}
			}
			if(index!=-1){
				diemthi[(short)(index+1)]=diem;
			}
			else{
				ISOException.throwIt(ISO7816.SW_DATA_INVALID);
			}
			apdu.setOutgoing();
			apdu.setOutgoingLength((short)10);
			apdu.sendBytesLong(diemthi,(short)0,(short)10);
			break;
		case INS_XOA:
			short x=0;
				index =(short)-1;
			for(i=0;i<(short)(diemlen-1);i+=2){
				if(id==diemthi[i]){
					index=i;
					break;
				}
			}
			if(index!=-1){
				if(index==(short)(diemlen-2)){
					diemthi[(short)index]=(byte)0x00;
					diemthi[(short)(index+1)]=(byte)0x00;
				}
				else{
					for(x=index;x<(short)(diemlen-2);x++){
						diemthi[x]=diemthi[(short)(x+2)];
					diemthi[(short)(x+2)]=(byte)0x00;
					}
				}
			}
			else{
				ISOException.throwIt(ISO7816.SW_DATA_INVALID);
			}
			apdu.setOutgoing();
			apdu.setOutgoingLength((short)10);
			apdu.sendBytesLong(diemthi,(short)0,(short)10);
			break;
		default:
			ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
		}
	}

}
