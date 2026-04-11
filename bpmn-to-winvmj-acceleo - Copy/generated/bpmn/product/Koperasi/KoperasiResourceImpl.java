// @generated from Koperasi.bpmn2

package bpmn.product.Koperasi;

import java.util.*;
import vmj.routing.route.VMJExchange;
import vmj.routing.route.Route;

public class KoperasiResourceImpl extends KoperasiResourceComponent {

    static class ProcessInstance {
        String id;
        String state;
        ProcessInstance(String id, String state) {
            this.id = id;
            this.state = state;
        }
    }

    static interface ProcessService {
        boolean upsert(ProcessInstance state); 
        List<ProcessInstance> getAllById(String id);
    }

    static class ProcessServiceImpl implements ProcessService {
        private static final List<ProcessInstance> STORE = new ArrayList<>();

        public boolean upsert(ProcessInstance state) {
            if (STORE.contains(state)) return true;
            return STORE.add(state);
        }

        public List<ProcessInstance> getAllById(String id) {
            return STORE.stream()
                .filter(p -> id.equals(p.id))
                .toList();
        }
    }

    static interface KoperasiService {
        void pilihMetodePembayaran(Map<String, Object> body, String processid);
        void melakukanVerifikasiTransaksi(Map<String, Object> body, String processid);
        void memilihJenisSimpanan(Map<String, Object> body, String processid);
        void updateSaldoSimpanan(Map<String, Object> body, String processid);
        void masukKePenyetoran(Map<String, Object> body, String processid);
        void melakukanPembayaran(Map<String, Object> body, String processid);
        void buatTransaksiSimpanan(Map<String, Object> body, String processid);
        void memasukkanNominal(Map<String, Object> body, String processid);
        void menolakPembayaran(Map<String, Object> body, String processid);

    }

    static class KoperasiServiceImpl implements KoperasiService {
	    @Override
	    public void pilihMetodePembayaran(Map<String, Object> body, String processid) {
	        // TODO: Implement logic for pilih metode pembayaran
			processService.upsert(new ProcessInstance(processid, "pilihMetodePembayaran"));
	        System.out.println("Executing pilih metode pembayaran");
	    }

	    @Override
	    public void melakukanVerifikasiTransaksi(Map<String, Object> body, String processid) {
	        // TODO: Implement logic for melakukan verifikasi transaksi
			processService.upsert(new ProcessInstance(processid, "melakukanVerifikasiTransaksi"));
	        System.out.println("Executing melakukan verifikasi transaksi");
	    }

	    @Override
	    public void memilihJenisSimpanan(Map<String, Object> body, String processid) {
	        // TODO: Implement logic for memilih jenis simpanan
			processService.upsert(new ProcessInstance(processid, "memilihJenisSimpanan"));
	        System.out.println("Executing memilih jenis simpanan");
	    }

	    @Override
	    public void updateSaldoSimpanan(Map<String, Object> body, String processid) {
	        // TODO: Implement logic for update saldo simpanan
			processService.upsert(new ProcessInstance(processid, "updateSaldoSimpanan"));
	        System.out.println("Executing update saldo simpanan");
	    }

	    @Override
	    public void masukKePenyetoran(Map<String, Object> body, String processid) {
	        // TODO: Implement logic for masuk ke penyetoran
			processService.upsert(new ProcessInstance(processid, "masukKePenyetoran"));
	        System.out.println("Executing masuk ke penyetoran");
	    }

	    @Override
	    public void melakukanPembayaran(Map<String, Object> body, String processid) {
	        // TODO: Implement logic for Melakukan pembayaran
			processService.upsert(new ProcessInstance(processid, "melakukanPembayaran"));
	        System.out.println("Executing Melakukan pembayaran");
	    }

	    @Override
	    public void buatTransaksiSimpanan(Map<String, Object> body, String processid) {
	        // TODO: Implement logic for buat transaksi simpanan
			processService.upsert(new ProcessInstance(processid, "buatTransaksiSimpanan"));
	        System.out.println("Executing buat transaksi simpanan");
	    }

	    @Override
	    public void memasukkanNominal(Map<String, Object> body, String processid) {
	        // TODO: Implement logic for memasukkan nominal
			processService.upsert(new ProcessInstance(processid, "memasukkanNominal"));
	        System.out.println("Executing memasukkan nominal");
	    }

	    @Override
	    public void menolakPembayaran(Map<String, Object> body, String processid) {
	        // TODO: Implement logic for menolak pembayaran
			processService.upsert(new ProcessInstance(processid, "menolakPembayaran"));
	        System.out.println("Executing menolak pembayaran");
	    }


    }

    private static ProcessService processService = new ProcessServiceImpl();
	private static KoperasiService koperasiService = new KoperasiServiceImpl();

    @Route(url = "call/pilihMetodePembayaran")
    public Map<String, Object> pilihMetodePembayaran(VMJExchange vmjExchange) {
        Map<String, Object> res = new HashMap<>();
		Map<String, Object> requestBody = vmjExchange.getPayload();

		String processid = (String) requestBody.get("processInstanceId");
		if (vmjExchange.getHttpMethod().equals("POST")) {

	        // Cek apakah step sebelumnya pernah dilakukan
	        // This also allows user yang mundur page trus isi form ulang
	        // karena langkah sebelum page ini pasti udh dilakukan
	        // ini juga mencegah orang dari asal tembak api
	        List<ProcessInstance> processes = processService.getAllById(processid);
			
			if (!(hasTaskState(processes, "simpananPokokWajib") || 
			    hasTaskState(processes, "memasukkanNominal"))
			) {
				res.put("status", "fail");
				res.put("message", "pilihMetodePembayaran DENIED");
            	return res;
			}
		}

        res.put("status", "ok");
        res.put("message", "pilihMetodePembayaran SUCCESS");

		koperasiService.pilihMetodePembayaran(requestBody, processid);
		koperasiService.melakukanPembayaran(requestBody, processid);

        return res;
    }

    @Route(url = "call/melakukanVerifikasiTransaksi")
    public Map<String, Object> melakukanVerifikasiTransaksi(VMJExchange vmjExchange) {
        Map<String, Object> res = new HashMap<>();
		Map<String, Object> requestBody = vmjExchange.getPayload();

		String processid = (String) requestBody.get("processInstanceId");
		if (vmjExchange.getHttpMethod().equals("POST")) {

	        // Cek apakah step sebelumnya pernah dilakukan
	        // This also allows user yang mundur page trus isi form ulang
	        // karena langkah sebelum page ini pasti udh dilakukan
	        // ini juga mencegah orang dari asal tembak api
	        List<ProcessInstance> processes = processService.getAllById(processid);
			
			if (!(hasTaskState(processes, "melakukanPembayaran"))
			) {
				res.put("status", "fail");
				res.put("message", "melakukanVerifikasiTransaksi DENIED");
            	return res;
			}
		}

        res.put("status", "ok");
        res.put("message", "melakukanVerifikasiTransaksi SUCCESS");

		koperasiService.melakukanVerifikasiTransaksi(requestBody, processid);
		boolean tidakBerhasil = true;
		boolean berhasil = true;
		if (tidakBerhasil) {
		    processService.upsert(new ProcessInstance(processid, "tidakBerhasil"));
		    koperasiService.menolakPembayaran(requestBody, processid);
		}
		else if (berhasil) {
		    processService.upsert(new ProcessInstance(processid, "berhasil"));
		    koperasiService.updateSaldoSimpanan(requestBody, processid);
		    koperasiService.buatTransaksiSimpanan(requestBody, processid);
		}

        return res;
    }

    @Route(url = "call/memilihJenisSimpanan")
    public Map<String, Object> memilihJenisSimpanan(VMJExchange vmjExchange) {
        Map<String, Object> res = new HashMap<>();
		Map<String, Object> requestBody = vmjExchange.getPayload();

		String processid = (String) requestBody.get("processInstanceId");
		if (vmjExchange.getHttpMethod().equals("POST")) {

	        // Cek apakah step sebelumnya pernah dilakukan
	        // This also allows user yang mundur page trus isi form ulang
	        // karena langkah sebelum page ini pasti udh dilakukan
	        // ini juga mencegah orang dari asal tembak api
	        List<ProcessInstance> processes = processService.getAllById(processid);
			
			if (!(hasTaskState(processes, "masukKePenyetoran"))
			) {
				res.put("status", "fail");
				res.put("message", "memilihJenisSimpanan DENIED");
            	return res;
			}
		}

        res.put("status", "ok");
        res.put("message", "memilihJenisSimpanan SUCCESS");

		koperasiService.memilihJenisSimpanan(requestBody, processid);
		boolean simpananSukarela = true;
		boolean simpananPokokWajib = true;
		if (simpananPokokWajib) {
		    processService.upsert(new ProcessInstance(processid, "simpananPokokWajib"));
		}
		else if (simpananSukarela) {
		    processService.upsert(new ProcessInstance(processid, "simpananSukarela"));
		    return res;
		}
		

        return res;
    }

    @Route(url = "call/masukKePenyetoran")
    public Map<String, Object> masukKePenyetoran(VMJExchange vmjExchange) {
        Map<String, Object> res = new HashMap<>();
		Map<String, Object> requestBody = vmjExchange.getPayload();

        String processid = UUID.randomUUID().toString();
        processService.upsert(new ProcessInstance(processid, "masukKePenyetoran"));
		res.put("processid", processid);

        res.put("status", "ok");
        res.put("message", "masukKePenyetoran SUCCESS");

		koperasiService.masukKePenyetoran(requestBody, processid);
		

        return res;
    }

    @Route(url = "call/memasukkanNominal")
    public Map<String, Object> memasukkanNominal(VMJExchange vmjExchange) {
        Map<String, Object> res = new HashMap<>();
		Map<String, Object> requestBody = vmjExchange.getPayload();

		String processid = (String) requestBody.get("processInstanceId");
		if (vmjExchange.getHttpMethod().equals("POST")) {

	        // Cek apakah step sebelumnya pernah dilakukan
	        // This also allows user yang mundur page trus isi form ulang
	        // karena langkah sebelum page ini pasti udh dilakukan
	        // ini juga mencegah orang dari asal tembak api
	        List<ProcessInstance> processes = processService.getAllById(processid);
			
			if (!(hasTaskState(processes, "simpananSukarela"))
			) {
				res.put("status", "fail");
				res.put("message", "memasukkanNominal DENIED");
            	return res;
			}
		}

        res.put("status", "ok");
        res.put("message", "memasukkanNominal SUCCESS");

		koperasiService.memasukkanNominal(requestBody, processid);
		

        return res;
    }

	private static boolean hasTaskState(List<ProcessInstance> processes, String... states) {
	    return processes.stream()
	        .anyMatch(x -> Arrays.stream(states)
	            .anyMatch(state -> x.state.equalsIgnoreCase(state)));
	}
}
