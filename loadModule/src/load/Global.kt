package load

import java.io.File


class Global {
    companion object {
        val isApp = false
        val projectPath =if (isApp)
                    File(Global::class.java.getProtectionDomain().getCodeSource().getLocation().getFile()).getParent()
                 else
                    File(Global::class.java.getResource("/").file).getParent()


    }

}