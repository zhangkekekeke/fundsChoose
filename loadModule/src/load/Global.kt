package load

import java.io.File


class Global {
    companion object {
        val isApp = false
        val projectPath = if (isApp)
            File(Global::class.java.getProtectionDomain().getCodeSource().getLocation().getFile()).getParent()
        else
            File(Global::class.java.getResource("/").file).getParent()

        val eastCookie =
            "AUTH_FUND.EASTMONEY.COM_GSJZ=AUTH*TTJJ*TOKEN; qgqp_b_id=7449707830b7730c528fd8bef268bae7; em_hq_fls=js; em-quote-version=topspeed; intellpositionL=1522.39px; intellpositionT=455px; HAList=f-0-000987-%u5168%u6307%u6750%u6599%2Ca-sz-300981-%u4E2D%u7EA2%u533B%u7597%2Ca-sz-000933-%u795E%u706B%u80A1%u4EFD%2Ca-sz-002585-%u53CC%u661F%u65B0%u6750%2Ca-sh-601636-%u65D7%u6EE8%u96C6%u56E2%2Ca-sz-300037-%u65B0%u5B99%u90A6%2Ca-sz-300671-%u5BCC%u6EE1%u7535%u5B50%2Ca-sh-600884-%u6749%u6749%u80A1%u4EFD%2Ca-sh-600036-%u62DB%u5546%u94F6%u884C%2Ca-sh-601012-%u9686%u57FA%u80A1%u4EFD%2Ca-sh-600276-%u6052%u745E%u533B%u836F%2Ca-sz-301047-%u4E49%u7FD8%u795E%u5DDE%2Ca-sh-605011-%u676D%u5DDE%u70ED%u7535; EMFUND0=09-05%2019%3A18%3A05@%23%24%u5E7F%u53D1%u53EF%u8F6C%u503A%u503A%u5238A@%23%24006482; EMFUND1=09-05%2019%3A20%3A05@%23%24%u534E%u5B9D%u53EF%u8F6C%u503A%u503A%u5238A@%23%24240018; EMFUND2=09-05%2019%3A21%3A24@%23%24%u534E%u5546%u53EF%u8F6C%u503A%u503A%u5238A@%23%24005273; EMFUND3=09-05%2019%3A43%3A34@%23%24%u535A%u9053%u4E2D%u8BC1500%u589E%u5F3AA@%23%24006593; EMFUND4=09-05%2019%3A45%3A06@%23%24%u5E7F%u53D1%u4E2D%u8BC1%u5168%u6307%u539F%u6750%u6599ETF@%23%24159944; EMFUND5=09-13%2022%3A22%3A30@%23%24%u897F%u90E8%u5229%u5F97%u4E2D%u8BC1500%u6307%u6570%u589E%u5F3AC@%23%24009300; EMFUND6=09-17%2022%3A21%3A43@%23%24%u6C47%u6DFB%u5BCC%u6CAA%u6DF1300%u5B89%u4E2D%u6307%u6570@%23%24000368; EMFUND7=09-17%2022%3A18%3A24@%23%24%u957F%u4FE1%u6CAA%u6DF1300%u6307%u6570%u589E%u5F3AA@%23%24005137; EMFUND8=09-17%2022%3A20%3A07@%23%24%u957F%u4FE1%u6CAA%u6DF1300%u6307%u6570%u589E%u5F3AC@%23%24007448; EMFUND9=09-26 21:42:38@#$%u5BCC%u8363%u6CAA%u6DF1300%u6307%u6570%u589E%u5F3AA@%23%24004788; st_si=75887540829216; st_asi=delete; st_pvi=47100283951626; st_sp=2021-08-31%2022%3A33%3A13; st_inirUrl=http%3A%2F%2Ffund.eastmoney.com%2Fcompare%2F; st_sn=2; st_psi=2021092621461848-112200312939-8614350342"

        val qie_x_sign="16326640023369081689B4ACDF2443BD0A00E3550AF57"
        val x_request_id="albus.59E7A019AD4E9B101AE4"
        val sensors_anonymous_id="179f07ccf2e129-030f0c7c2dd2ca-3e604809-2073600-179f07ccf2f67e"
    }

}