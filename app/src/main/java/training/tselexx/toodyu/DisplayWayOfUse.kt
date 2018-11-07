package training.tselexx.toodyu

import android.app.AlertDialog
import com.tselexx.toodyu.Common_Util_functions.Companion.contx
import com.tselexx.toodyu.R

class DisplayWayOfUse {

    public fun displayInfo() {
        val alert = AlertDialog.Builder(contx, R.style.AlertDialogTheme)
        with(alert) {
            setTitle(contx!!.getString(R.string.explications))
            setMessage(contx!!.getString(R.string.info100))
            setPositiveButton("OK") { dialog, whichButton ->
                dialog.dismiss()
            }
        }
        // Dialog
        val dialog = alert.create()
        dialog.show()
    }
}