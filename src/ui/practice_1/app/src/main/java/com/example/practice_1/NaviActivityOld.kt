import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.practice_1.HomeFragment
import com.example.practice_1.R
import com.example.practice_1.RecordFragment
import com.example.practice_1.databinding.ActivityNaviBinding

private const val TAG_HOME = "home_fragment"
private const val TAG_RECORD = "record_fragment"

class NaviActivityOld : AppCompatActivity() {

    private lateinit var binding : ActivityNaviBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNaviBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setFragment(TAG_HOME, HomeFragment())

        binding.navigationView.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.homeFragment -> setFragment(TAG_HOME, HomeFragment())
                R.id.recordFragment-> setFragment(TAG_RECORD, RecordFragment())
            }
            true
        }
    }

    private fun setFragment(tag: String, fragment: Fragment) {
        val manager: FragmentManager = supportFragmentManager
        val fragTransaction = manager.beginTransaction()

        if (manager.findFragmentByTag(tag) == null){
            fragTransaction.add(R.id.mainFrameLayout, fragment, tag)
        }

        val home = manager.findFragmentByTag(TAG_HOME)
        val record = manager.findFragmentByTag(TAG_RECORD)


        if (home != null){
            fragTransaction.hide(home)
        }

        if (record != null) {
            fragTransaction.hide(record)
        }

        if (tag == TAG_HOME) {
            if (home != null) {
                fragTransaction.show(home)
            }
        }

        else if (tag == TAG_RECORD){
            if (record != null){
                fragTransaction.show(record)
            }
        }

        fragTransaction.commitAllowingStateLoss()
    }
}