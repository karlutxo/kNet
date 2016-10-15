/**
 * Created by carlos on 21/9/16.
 */

import java.text.SimpleDateFormat

class kGetUpdatedFile {

    def config
    def fmtdia = new SimpleDateFormat("YYYYMMdd")

    kGetUpdatedFile () {

        config = new ConfigSlurper().parse(new File('src/config.groovy').toURL())
        config.UpdateFiles.each {
            try {
                println "Descargando " + config.UpdateURL + it + " como " + it+"_tmp"
                def date = new Date()

                def filetmp = new FileOutputStream(it+"_tmp")
                def out = new BufferedOutputStream(filetmp)
                out << new URL(config.UpdateURL + it).openStream()
                out.close()


                def currentfile = new File(it)
                println "Renombro $currentfile.name como " + currentfile.name+ "_"+fmtdia.format(date)
                currentfile.renameTo(currentfile.name+ "_"+fmtdia.format(date))
                def newfile = new File(it+"_tmp")
                println "Renombro $newfile.name como $it"
                newfile.renameTo(it)
            }
            catch (all) {println all }
        }
    }
}



