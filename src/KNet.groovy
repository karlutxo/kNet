import java.text.SimpleDateFormat

/**
 * Created by carlos on 7/9/16
 */
class KNet {

    def Version = "1.1_27.09.2016"
    def PrivateIP
    def PublicIP
    def Gateway
    def config
    def fmthora = new SimpleDateFormat("HH:mm:ss")
    def fmtdiahora = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss")
    File file
    def MailError

    KNet() {

        config = new ConfigSlurper().parse(new File('src/config.groovy').toURL())
        SendUnsentFiles()

        def date = new Date()
        PrivateIP = InetAddress.localHost.hostAddress

        file = new File("knet_" + fmtdiahora.format(date) + ".txt")
        file.write("<html><body>"+config.Puesto + "  " + fmtdiahora.format(date) + "   kNet version: " + Version)
        file.append("  PRIVATE IP: "+ PrivateIP + "<br><br>")

        config.Comandos.each {
            try { file.append("<b>*** " + it+": </b><br>" + it.execute().text.replaceAll("\n", "<br>\n") + "<br><br>\n\n") }
            catch (all) {file.append("<b>" + all + "</b><br><br>\n\n")}
        }

        TestURLS()
        file.append("</body></html>")
        SendUnsentFiles()
        DeleteOldSentFiles()
    }

    void SendUnsentFiles () {
        new File(".").eachFileMatch(~/.*.txt/) {file ->
            if (SendMail(file.text)) {
                file.renameTo(file.name+ "_sent")
            } else {
                def date = new Date()
                file.append("<br><<b>MAIL NOT SENT AT:"+ fmtdiahora.format(date) + "<br>" + MailError + "</b><br><br>\n\n")
            }
        }
    }

    void DeleteOldSentFiles () {
        def ConservarDias = 7
        new File(".").eachFileMatch(~/.*.txt_sent/) {file ->
            long diff = new Date().getTime() - file.lastModified();
//            println file.name + ' ' + file.lastModified() + ' ' + (diff / 1000 /60)
            if (diff > ConservarDias * 24 * 60 * 60 * 1000) {
                file.delete();
            }
        }
    }


    String TestURLS() {
        def i
        def date
        file.append('<table border="1"><tr><th style="text-align:center">Nº</th><th style="text-align:center">Hora</th>')
        config.URLs.each {
            file.append('<th style="text-align:center">'+it+'</th>')
        }
        file.append("</tr>")
        for (i = 1; i <= config.Repeticiones; i++) {
            date = new Date()
            file.append('<tr><td style="text-align:center">'+i+'</td><td style="text-align:center">'+fmthora.format(date)+"</td>")
            config.URLs.each {TestURL(it)}
            file.append("</tr>\n")
            sleep(config.Pausa*1000)
        }
        file.append("</table>")
    }

    String TestURL(String cUrl) {
        try {
//            file.append("<td>" + InetAddress.getByName(cUrl))
            file.append('<td style="text-align:center">')
            long startTime = System.currentTimeMillis();
            def page = new URL("http://" + cUrl).text
            long elapsedTime = (System.currentTimeMillis() - startTime);
            file.append(elapsedTime + " ms")
            page.eachLine {
                if (it.contains("Su IP:")) {
                    def (val1, val2) = it.tokenize("<")
                    file.append("<br><small>"+val1.replace("Su IP:","")+"</small><br>")
                }
            }
            file.append("</td>")
        } catch (all) {file.append("<td>" + all + "</td>")}
    }

    boolean SendMail(String mensaje) {
        def mail = new KMail()
        mail.setUser(config.MailUser)
        mail.setPassword(config.MailPassword)
        mail.setDest(config.MailDestino)
        mail.setSubject("Pruebas Conexión desde " + config.Puesto)
        mail.setMsg(mensaje)
        if (mail.send()) {
            return true
        } else {
            MailError = mail.Resultado
            return false
        }
    }
}