// @generated from Koperasi.bpmn2

package bpmn.product.Koperasi;

import java.util.*;
import vmj.routing.route.VMJExchange;
import vmj.hibernate.integrator.RepositoryUtil;

public abstract class KoperasiResourceDecorator extends KoperasiResourceComponent {
	protected KoperasiResourceComponent record;
	
	public KoperasiResourceDecorator(KoperasiResourceComponent record) {
        this.record = record;
    }

    public Map<String, Object> pilihMetodePembayaran(VMJExchange vmjExchange) {
		return record.pilihMetodePembayaran(vmjExchange);
	}
    public Map<String, Object> melakukanVerifikasiTransaksi(VMJExchange vmjExchange) {
		return record.melakukanVerifikasiTransaksi(vmjExchange);
	}
    public Map<String, Object> memilihJenisSimpanan(VMJExchange vmjExchange) {
		return record.memilihJenisSimpanan(vmjExchange);
	}
    public Map<String, Object> masukKePenyetoran(VMJExchange vmjExchange) {
		return record.masukKePenyetoran(vmjExchange);
	}
    public Map<String, Object> memasukkanNominal(VMJExchange vmjExchange) {
		return record.memasukkanNominal(vmjExchange);
	}
}
