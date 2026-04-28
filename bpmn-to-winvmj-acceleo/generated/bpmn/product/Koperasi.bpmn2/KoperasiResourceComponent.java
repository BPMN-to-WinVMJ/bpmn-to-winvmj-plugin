// @generated from Koperasi.bpmn2

package .core;

import java.util.*;
import vmj.routing.route.VMJExchange;
import vmj.hibernate.integrator.RepositoryUtil;

public abstract class KoperasiResourceComponent implements KoperasiResource {

    public abstract Map<String, Object> masukKePenyetoran(VMJExchange vmjExchange);
    public abstract Map<String, Object> memasukkanNominal(VMJExchange vmjExchange);
    public abstract Map<String, Object> pilihMetodePembayaran(VMJExchange vmjExchange);
    public abstract Map<String, Object> memilihJenisSimpanan(VMJExchange vmjExchange);
    public abstract Map<String, Object> melakukanVerifikasiTransaksi(VMJExchange vmjExchange);
}
