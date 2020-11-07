package uz.muhammayusuf.kurbonov.mycafe.viewmodels.chefMode

import androidx.lifecycle.Observer
import org.junit.Test
import uz.muhammayusuf.kurbonov.mycafe.models.QueryResult

class ManagerModeViewModelTest {

    private val model = ChefModeViewModel()

    private val listener = object : Observer<QueryResult> {

        override fun onChanged(t: QueryResult?) {
            when (t) {
                is QueryResult.Loading -> println("Loading ...")
                is QueryResult.Error -> {
                    model.managerData.removeObserver(this)
                }
            }
        }
    }


    @Test
    fun startListening() {
        model.startListening()

    }

    @Test
    fun setCompleted() {
    }
}