package com.clique.retire.util;

import java.rmi.Naming;
import java.rmi.RemoteException;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.Fidelity;
import javax.print.attribute.standard.MediaTray;
import javax.print.attribute.standard.OrientationRequested;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.araujo.drogatel.print.PrintServerRemote;
import com.clique.retire.enums.ParametroEnum;
import com.clique.retire.repository.drogatel.DrogatelParametroRepository;

@Component
public class PrintUtil {

	@Autowired
	private DrogatelParametroRepository parametroRepository;

	public static PrintService getPrint(String printName) {
	   PrintService print =null;
	   PrintService []impressoras = PrintServiceLookup.lookupPrintServices(null,null);
       for(int i=0; i< impressoras.length; i++){
    	   print = impressoras [i];
    	   if(print.getName().replace("\\", "").equalsIgnoreCase(printName)){
    		   return print;
    	   }
       }
       
       return null;
	}

	public boolean printOnDemand(String nomeImpressora, byte[] documentoImpressao) throws RemoteException {
		String paramCaminhoServidor = parametroRepository.findByNome(ParametroEnum.SERVIDOR_IMPRESSAO_DROGATEL.getDescricao()).getValor();
		
		if (paramCaminhoServidor != null) {
			PrintServerRemote remote = null;
			try {
				remote = (PrintServerRemote) Naming.lookup(paramCaminhoServidor);
			} catch (Exception e) {
				throw new RemoteException("Erro ao tentar se conectar com o servidor: " + paramCaminhoServidor, e.getCause());
			}
			if (remote != null) {
				remote.printOnDemand(documentoImpressao, nomeImpressora);
				return true;
			}
		}
		
		return false;
	}
		
	 public static void printDoc(String printName, Object source, DocFlavor typee ){
	
	   PrintService print = getPrint(printName);

	   if(print==null){
		   return;
	   }

	   PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
       aset.add(new Copies(1));
       aset.add(OrientationRequested.PORTRAIT);
       aset.add(MediaTray.LARGE_CAPACITY);
       aset.add(Fidelity.FIDELITY_FALSE);

       Doc myDoc = new SimpleDoc(source, typee, null);
	   DocPrintJob job = print.createPrintJob();
       try {
               job.print(myDoc, aset);
       } catch (PrintException pe) {
    	   pe.printStackTrace();
       }
   }
}