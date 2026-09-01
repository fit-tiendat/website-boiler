package vn.yhct.club;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.*;

public class MainActivity extends Activity {
    private LinearLayout root;
    private String role = "MEMBER";

    private final int CREAM = Color.rgb(247,241,226);
    private final int PAPER = Color.rgb(255,251,241);
    private final int GREEN = Color.rgb(48,86,63);
    private final int GREEN_DARK = Color.rgb(33,64,46);
    private final int GOLD = Color.rgb(169,126,52);
    private final int BROWN = Color.rgb(91,69,51);
    private final int MUTED = Color.rgb(108,104,91);

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        showHome();
    }

    private GradientDrawable bg(int color, float radius) {
        GradientDrawable g = new GradientDrawable();
        g.setColor(color);
        g.setCornerRadius(radius);
        return g;
    }

    private TextView text(String s, int sp, boolean bold) {
        TextView v = new TextView(this);
        v.setText(s);
        v.setTextSize(sp);
        v.setTextColor(GREEN_DARK);
        v.setPadding(8,8,8,8);
        v.setTypeface(Typeface.SERIF, bold ? Typeface.BOLD : Typeface.NORMAL);
        return v;
    }

    private TextView small(String s) {
        TextView v = text(s,13,false);
        v.setTextColor(MUTED);
        return v;
    }

    private Space gap(int h) {
        Space s = new Space(this);
        s.setLayoutParams(new LinearLayout.LayoutParams(1,h));
        return s;
    }

    private Button btn(String label, View.OnClickListener l) {
        Button b = new Button(this);
        b.setText(label);
        b.setTextSize(16);
        b.setTextColor(Color.WHITE);
        b.setAllCaps(false);
        b.setTypeface(Typeface.SERIF, Typeface.BOLD);
        b.setPadding(18,14,18,14);
        b.setBackground(bg(GREEN,28));
        b.setOnClickListener(l);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1,-2);
        p.setMargins(0,7,0,7);
        b.setLayoutParams(p);
        return b;
    }

    private Button outlineBtn(String label, View.OnClickListener l) {
        Button b = new Button(this);
        b.setText(label);
        b.setTextSize(15);
        b.setTextColor(GREEN_DARK);
        b.setAllCaps(false);
        GradientDrawable g=bg(PAPER,28); g.setStroke(2,GOLD); b.setBackground(g);
        b.setOnClickListener(l);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1,-2);
        p.setMargins(0,6,0,6); b.setLayoutParams(p);
        return b;
    }

    private void base(String title, String subtitle) {
        ScrollView sv = new ScrollView(this);
        sv.setBackgroundColor(CREAM);
        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(24,22,24,46);
        sv.addView(root);

        LinearLayout hero = new LinearLayout(this);
        hero.setOrientation(LinearLayout.VERTICAL);
        hero.setPadding(22,20,22,20);
        GradientDrawable heroBg = bg(GREEN_DARK,34);
        heroBg.setStroke(2, GOLD);
        hero.setBackground(heroBg);

        TextView crest = new TextView(this);
        crest.setText("☯  藥  YHCT");
        crest.setTextColor(Color.rgb(235,214,163));
        crest.setTextSize(17);
        crest.setGravity(Gravity.CENTER_HORIZONTAL);
        crest.setTypeface(Typeface.SERIF, Typeface.BOLD);
        hero.addView(crest);

        TextView h = new TextView(this);
        h.setText(title);
        h.setTextColor(Color.WHITE);
        h.setTextSize(25);
        h.setGravity(Gravity.CENTER_HORIZONTAL);
        h.setTypeface(Typeface.SERIF, Typeface.BOLD);
        h.setPadding(0,8,0,2);
        hero.addView(h);

        TextView sub = new TextView(this);
        sub.setText(subtitle);
        sub.setTextColor(Color.rgb(236,226,198));
        sub.setTextSize(14);
        sub.setGravity(Gravity.CENTER_HORIZONTAL);
        sub.setTypeface(Typeface.SERIF, Typeface.NORMAL);
        hero.addView(sub);

        root.addView(hero);
        root.addView(gap(16));
    }

    private LinearLayout panel() {
        LinearLayout p = new LinearLayout(this);
        p.setOrientation(LinearLayout.VERTICAL);
        p.setPadding(18,16,18,16);
        GradientDrawable g=bg(PAPER,26); g.setStroke(1,Color.rgb(220,207,173)); p.setBackground(g);
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2); lp.setMargins(0,6,0,8); p.setLayoutParams(lp);
        return p;
    }

    private TextView section(String s) {
        TextView v=text(s,19,true); v.setTextColor(BROWN); v.setPadding(4,14,4,7); return v;
    }

    private LinearLayout stat(String value, String label) {
        LinearLayout p=panel();
        TextView a=text(value,24,true); a.setTextColor(GREEN_DARK); a.setGravity(Gravity.CENTER_HORIZONTAL);
        TextView b=small(label); b.setGravity(Gravity.CENTER_HORIZONTAL);
        p.addView(a); p.addView(b); return p;
    }

    private void showHome() {
        base("CLB Y HỌC CỔ TRUYỀN", "Gìn giữ tinh hoa • Kết nối hội viên • Phụng sự cộng đồng");

        LinearLayout quote=panel();
        TextView q=text("“Dưỡng sinh quý ở điều hòa, hành y quý ở nhân tâm.”",17,true); q.setTextColor(GOLD); q.setGravity(Gravity.CENTER_HORIZONTAL);
        quote.addView(q);
        quote.addView(small("Ứng dụng quản lý hội viên • Phiên bản 2.0"));
        root.addView(quote);

        root.addView(section("Tổng quan câu lạc bộ"));
        LinearLayout stats=new LinearLayout(this); stats.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout a=stat("200","Hội viên"), b=stat("12","Hoạt động"), c=stat("145","Điểm mẫu");
        stats.addView(a,new LinearLayout.LayoutParams(0,-2,1));
        stats.addView(b,new LinearLayout.LayoutParams(0,-2,1));
        stats.addView(c,new LinearLayout.LayoutParams(0,-2,1));
        root.addView(stats);

        root.addView(section("Vai trò truy cập"));
        Spinner s=new Spinner(this);
        String[] roles={"MEMBER","MOD","ADMIN"};
        ArrayAdapter<String> ad=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,roles);
        s.setAdapter(ad); s.setSelection(role.equals("ADMIN")?2:role.equals("MOD")?1:0);
        s.setBackground(bg(PAPER,20)); s.setPadding(12,10,12,10);
        s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> p,View v,int pos,long id){role=roles[pos];}
            public void onNothingSelected(AdapterView<?> p){}
        }); root.addView(s);

        root.addView(section("Danh mục"));
        root.addView(btn("👤  Tra cứu thành viên",v->memberLookup()));
        root.addView(btn("🌿  Hoạt động câu lạc bộ",v->activities()));
        root.addView(btn("🏅  Điểm & khen thưởng",v->points()));
        root.addView(btn("📜  Tin tức & thông báo",v->news()));
        root.addView(outlineBtn("⚙  Khu vực quản trị",v->admin()));

        root.addView(section("Tôn chỉ"));
        LinearLayout mission=panel();
        mission.addView(text("Kết nối người làm và yêu thích Y học cổ truyền; khuyến khích học tập, chia sẻ chuyên môn, dưỡng sinh và hoạt động cộng đồng trên nền tảng minh bạch, nhân văn.",16,false));
        root.addView(mission);
    }

    private void memberLookup(){
        base("TRA CỨU HỘI VIÊN", "Hồ sơ • Chức danh • Điểm hoạt động");
        LinearLayout box=panel();
        box.addView(text("Thông tin tra cứu",18,true));
        EditText q=new EditText(this); q.setHint("Nhập mã hội viên hoặc họ tên"); q.setInputType(InputType.TYPE_CLASS_TEXT); q.setSingleLine(true); box.addView(q);
        TextView out=text("",16,false);
        box.addView(btn("Tra cứu",v->{
            String x=q.getText().toString().trim();
            if(x.isEmpty()) out.setText("Vui lòng nhập thông tin tra cứu.");
            else out.setText("NGUYỄN VĂN AN\nMã hội viên: TV001\nChức danh: Hội viên\nBan: Chuyên môn\nTổng điểm: 145\nXếp loại: Tích cực\n\nHuy hiệu: 🌿 Hội viên tích cực");
        })); box.addView(out); root.addView(box);
        root.addView(outlineBtn("← Về trang chủ",v->showHome()));
    }

    private void activities(){
        base("HOẠT ĐỘNG CLB", "Sinh hoạt • Chuyên môn • Cộng đồng");
        root.addView(section("Sắp diễn ra"));
        root.addView(activityCard("15/09/2026","Dưỡng sinh & điều tức","Sinh hoạt chuyên đề", "+10 điểm"));
        root.addView(activityCard("28/09/2026","Khám tư vấn sức khỏe cộng đồng","Thiện nguyện", "+20 điểm"));
        root.addView(activityCard("12/10/2026","Nhận biết và sử dụng dược liệu","Tập huấn", "+15 điểm"));
        if(role.equals("ADMIN")||role.equals("MOD")) root.addView(btn("+ Ghi nhận hoạt động",v->toast("Quyền MOD/ADMIN đã được mở ở bản V2 pilot.")));
        root.addView(outlineBtn("← Về trang chủ",v->showHome()));
    }

    private LinearLayout activityCard(String date,String title,String type,String score){
        LinearLayout l=panel();
        TextView d=text(date,13,true); d.setTextColor(GOLD); l.addView(d);
        TextView t=text(title,18,true); t.setTextColor(GREEN_DARK); l.addView(t);
        l.addView(small(type+"  •  "+score));
        return l;
    }

    private void points(){
        base("ĐIỂM & KHEN THƯỞNG", "Ghi nhận đóng góp của hội viên");
        LinearLayout badge=panel();
        TextView name=text("NGUYỄN VĂN AN • TV001",18,true); name.setGravity(Gravity.CENTER_HORIZONTAL); badge.addView(name);
        TextView num=text("145",34,true); num.setTextColor(GOLD); num.setGravity(Gravity.CENTER_HORIZONTAL); badge.addView(num);
        TextView label=small("TỔNG ĐIỂM HOẠT ĐỘNG"); label.setGravity(Gravity.CENTER_HORIZONTAL); badge.addView(label);
        badge.addView(text("🌿  Hội viên tích cực quý III/2026",17,true)); root.addView(badge);

        root.addView(section("Lịch sử ghi nhận"));
        root.addView(activityCard("+10","Sinh hoạt chuyên đề","Dưỡng sinh","15/08/2026"));
        root.addView(activityCard("+20","Khám tư vấn cộng đồng","Thiện nguyện","28/08/2026"));
        root.addView(activityCard("+15","Tập huấn dược liệu","Chuyên môn","30/08/2026"));
        root.addView(outlineBtn("← Về trang chủ",v->showHome()));
    }

    private void news(){
        base("TIN TỨC CLB", "Thông báo • Kiến thức • Hoạt động");
        root.addView(newsCard("01/09/2026","Ra mắt ứng dụng quản lý hội viên V2","Giao diện mới mang tinh thần Y học cổ truyền, hướng đến sử dụng đơn giản và trực quan."));
        root.addView(newsCard("25/08/2026","Chuẩn bị chương trình sức khỏe cộng đồng","CLB chuẩn bị hoạt động tư vấn sức khỏe và dưỡng sinh trong tháng 9."));
        root.addView(outlineBtn("← Về trang chủ",v->showHome()));
    }

    private LinearLayout newsCard(String date,String title,String body){
        LinearLayout l=panel(); TextView d=small(date); d.setTextColor(GOLD); l.addView(d); l.addView(text(title,18,true)); l.addView(text(body,15,false)); return l;
    }

    private void admin(){
        base("KHU VỰC QUẢN TRỊ", "Phân quyền theo vai trò");
        if(role.equals("MEMBER")){
            LinearLayout warn=panel(); warn.addView(text("🔒 Quyền truy cập hạn chế",19,true)); warn.addView(text("Tài khoản MEMBER chỉ được xem dữ liệu cá nhân và nội dung công khai. Vui lòng chuyển sang MOD hoặc ADMIN trong chế độ mô phỏng để xem khu vực quản trị.",15,false)); root.addView(warn);
        } else {
            LinearLayout identity=panel(); identity.addView(text("Đang truy cập với vai trò: "+role,18,true)); identity.addView(small(role.equals("ADMIN")?"Toàn quyền hệ thống":"Quản lý nghiệp vụ được phân công")); root.addView(identity);
            root.addView(btn("👥 Quản lý thành viên",v->toast("Danh sách thành viên - V2 demo")));
            root.addView(btn("📝 Chấm điểm hoạt động",v->toast("Biểu mẫu chấm điểm - V2 demo")));
            if(role.equals("ADMIN")){
                root.addView(btn("🎖 Bổ nhiệm MOD / chức danh",v->toast("Chức năng ADMIN - V2 demo")));
                root.addView(btn("⚙ Cấu hình hệ thống",v->toast("Cấu hình hệ thống - V2 demo")));
            }
        }
        root.addView(outlineBtn("← Về trang chủ",v->showHome()));
    }

    private void toast(String s){ Toast.makeText(this,s,Toast.LENGTH_SHORT).show(); }
}
