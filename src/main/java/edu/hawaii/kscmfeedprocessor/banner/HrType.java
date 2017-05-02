package edu.hawaii.kscmfeedprocessor.banner;

/**
 *
 */
public enum HrType {

    Credit("Credit", "Credit"), Lecture("Lec", "Lecture"), Lab("Lab", "Lab"), Other("Oth", "Other"), Billing("Bill", "Billing"), Contact("Cont",
            "Contact");

    private String bannerName;
    private String kscmName;

    HrType(String bannerName, String kscmName) {
        this.bannerName = bannerName;
        this.kscmName = kscmName;
    }

    public String getBannerName() {
        return bannerName;
    }

    public String getKscmName() {
        return kscmName;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        sb.append(String.format("name:%s", this.name()));
        sb.append(", ");
        sb.append(String.format("bannerName:%s", this.bannerName));
        sb.append(", ");
        sb.append(String.format("kscmName:%s", this.kscmName));
        sb.append('}');
        return sb.toString();
    }

    public static void main(String[] args)  {
        System.out.println(HrType.Credit.name());
        System.out.println(HrType.Lecture.name());
        System.out.println(HrType.Other.name());
        System.out.println(HrType.Credit);
        System.out.println(HrType.Lecture);
        System.out.println(HrType.Other);
    }

}
