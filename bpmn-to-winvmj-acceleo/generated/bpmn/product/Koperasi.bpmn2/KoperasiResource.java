// @generated from Koperasi.bpmn2

package .core;

import java.util.*;
import vmj.routing.route.VMJExchange;

public interface KoperasiResource {
    Map<String, Object> masukKePenyetoran(VMJExchange vmjExchange);
    Map<String, Object> memasukkanNominal(VMJExchange vmjExchange);
    Map<String, Object> pilihMetodePembayaran(VMJExchange vmjExchange);
    Map<String, Object> memilihJenisSimpanan(VMJExchange vmjExchange);
    Map<String, Object> melakukanVerifikasiTransaksi(VMJExchange vmjExchange);
}
