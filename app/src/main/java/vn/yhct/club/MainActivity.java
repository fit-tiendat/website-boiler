package vn.yhct.club;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.*;

public class MainActivity extends Activity {
    private LinearLayout root;
    private String role = "MEMBER";

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        showHome();
    }

    private TextView text(String s, int sp, boolean bold) {
        TextView v = new TextView(this);
        v.setText(s); v.setTextSize(sp); v.setTextColor(Color.rgb(25,45,35));
        v.setPadding(8,10,8,10);
        if (bold) v.setTypeface(null, 1);
        return v;
    }

    private Button btn(String label, View.OnClickListener l) {
        Button b = new Button(this); b.setText(label); b.setAllCaps(false); b.setOnClickListener(l);
        return b;
    }

    private void base(String title) {
        ScrollView sv = new ScrollView(this);
        root = new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setPadding(24,24,24,40);
        sv.addView(root);
        TextView h = text("☯  " + title, 24, true); h.setGravity(Gravity.CENTER_HORIZONTAL); root.addView(h);
        TextView sub = text("CLB Y Học Cổ Truyền • Pilot V1", 14, false); sub.setGravity(Gravity.CENTER_HORIZONTAL); root.addView(sub);
        setContentView(sv);
    }

    private void showHome() {
        base("TRANG TỔNG QUAN");
        Spinner s = new Spinner(this);
        String[] roles = {"MEMBER","MOD","ADMIN"};
        s.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, roles));
        s.setSelection(role.equals("ADMIN")?2:role.equals("MOD")?1:0);
        s.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener(){
            public void onItemSelected(android.widget.AdapterView<?> p, View v, int pos, long id){ role=roles[pos]; }
            public void onNothingSelected(android.widget.AdapterView<?> p){}
        });
        root.addView(text("Vai trò đang mô phỏng",15,true)); root.addView(s);
        root.addView(text("200 thành viên • 12 hoạt động • 4 khen thưởng",18,true));
        root.addView(text("Tôn chỉ: Kết nối hội viên, gìn giữ giá trị Y học cổ truyền, khuyến khích học tập và phục vụ cộng đồng.",16,false));
        root.addView(btn("Tra cứu thành viên", v -> memberLookup()));
        root.addView(btn("Hoạt động CLB", v -> activities()));
        root.addView(btn("Điểm & khen thưởng", v -> points()));
        root.addView(btn("Tin tức", v -> news()));
        root.addView(btn("Khu vực quản trị", v -> admin()));
    }

    private void memberLookup(){
        base("TRA CỨU THÀNH VIÊN");
        EditText q = new EditText(this); q.setHint("Nhập mã hoặc họ tên"); q.setInputType(InputType.TYPE_CLASS_TEXT); root.addView(q);
        TextView out = text("",16,false); root.addView(btn("Tra cứu", v -> {
            String x=q.getText().toString().trim();
            if(x.isEmpty()) out.setText("Vui lòng nhập thông tin tra cứu.");
            else out.setText("Kết quả mẫu\nNguyễn Văn An\nMã: TV001\nChức danh: Hội viên\nBan: Chuyên môn\nTổng điểm: 145\nXếp loại: Tích cực");
        })); root.addView(out); root.addView(btn("← Về trang chủ", v -> showHome()));
    }

    private void activities(){
        base("HOẠT ĐỘNG");
        root.addView(card("15/09/2026", "Sinh hoạt chuyên đề: Dưỡng sinh", "+10 điểm"));
        root.addView(card("28/09/2026", "Khám tư vấn cộng đồng", "+20 điểm"));
        root.addView(card("12/10/2026", "Tập huấn dược liệu", "+15 điểm"));
        if(role.equals("ADMIN")||role.equals("MOD")) root.addView(btn("+ Ghi nhận hoạt động (demo)", v -> Toast.makeText(this,"Đã mở quyền MOD/ADMIN ở bản pilot.",Toast.LENGTH_SHORT).show()));
        root.addView(btn("← Về trang chủ", v -> showHome()));
    }

    private LinearLayout card(String a,String b,String c){
        LinearLayout l=new LinearLayout(this); l.setOrientation(LinearLayout.VERTICAL); l.setPadding(18,14,18,14);
        l.addView(text(a,14,true)); l.addView(text(b,17,true)); l.addView(text(c,15,false));
        return l;
    }

    private void points(){
        base("ĐIỂM & KHEN THƯỞNG");
        root.addView(text("Nguyễn Văn An • TV001",18,true));
        root.addView(text("Tổng điểm hiện tại: 145",22,true));
        root.addView(text("• Sinh hoạt chuyên đề: +10\n• Khám tư vấn cộng đồng: +20\n• Tập huấn: +15\n• Hỗ trợ sự kiện: +10",16,false));
        root.addView(text("Khen thưởng: Hội viên tích cực quý III/2026",17,true));
        root.addView(btn("← Về trang chủ", v -> showHome()));
    }

    private void news(){
        base("TIN TỨC CLB");
        root.addView(text("01/09/2026 • CLB triển khai ứng dụng quản lý hội viên phiên bản thử nghiệm.",17,true));
        root.addView(text("25/08/2026 • Chuẩn bị chương trình tư vấn sức khỏe cộng đồng tháng 9.",16,false));
        root.addView(btn("← Về trang chủ", v -> showHome()));
    }

    private void admin(){
        base("QUẢN TRỊ");
        if(role.equals("MEMBER")) {
            root.addView(text("Bạn đang ở vai trò MEMBER. Khu vực này chỉ dành cho MOD hoặc ADMIN.",17,true));
        } else {
            root.addView(text("Vai trò: "+role,18,true));
            root.addView(btn("Quản lý thành viên", v -> toast("Danh sách thành viên - demo")));
            root.addView(btn("Chấm điểm hoạt động", v -> toast("Mở biểu mẫu chấm điểm - demo")));
            if(role.equals("ADMIN")) {
                root.addView(btn("Bổ nhiệm MOD / chức danh", v -> toast("Chức năng ADMIN - demo")));
                root.addView(btn("Cấu hình hệ thống", v -> toast("Cấu hình hệ thống - demo")));
            }
        }
        root.addView(btn("← Về trang chủ", v -> showHome()));
    }

    private void toast(String s){ Toast.makeText(this,s,Toast.LENGTH_SHORT).show(); }
}
