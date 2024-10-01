package com.example.qrscan.ui.fragment.setting

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.qrscan.Bridge
import com.example.qrscan.R
import com.example.qrscan.adapter.DeveloperSettingAdapter
import com.example.qrscan.adapter.SearchEngineAdapter
import com.example.qrscan.base.BaseFragment
import com.example.qrscan.data.model.DeveloperSetting
import com.example.qrscan.data.model.SearchEngineModel
import com.example.qrscan.databinding.FragmentSettingBinding
import com.example.qrscan.extension.setOnSingleClickListener
import com.example.qrscan.ui.dialog.AboutDialogFragment
import com.example.qrscan.ui.dialog.TryFreeDialogFragment

@Suppress("DEPRECATION")
@SuppressLint("UseCompatLoadingForDrawables")
class SettingFragment : BaseFragment<FragmentSettingBinding>() {
    private val sharedPreferences by lazy {
        requireActivity().getSharedPreferences(CONFIG_APP, Context.MODE_PRIVATE)
    }
    private val editConfig by lazy {
        sharedPreferences.edit()
    }
    private var isVibrate: Boolean = false
    private var isSound: Boolean = false
    private var positionAdapter = 0

    companion object {
        const val CONFIG_APP = "config_app"
        const val SAVE_SOUND = "save_sound"
        const val SAVE_VIBRATE = "save_vibrate"
        const val SAVE_SEARCH_ENGINE = "search_engine"
        const val POSITION = "position"
        const val GOOGLE = "Google"
        const val BING = "Bing"
        const val YAHOO = "Yahoo"
        const val DUCKDUCKGO = "DuckDuckGo"
        const val ECOSIA = "Ecosia"
        const val YANDEX = "Yandex"

        const val RATE = 0
        const val FEEDBACK = 1
        const val SHARE = 2
        const val PRIVACY = 3
        const val TERMS = 4
        const val ABOUT = 5
    }

    override fun getLayout(): Int = R.layout.fragment_setting
    override fun setInsets(left: Int, top: Int, right: Int, bottom: Int) {
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isVibrate = sharedPreferences.getBoolean(SAVE_VIBRATE, false)
        isSound = sharedPreferences.getBoolean(SAVE_SOUND, false)
        positionAdapter = sharedPreferences.getInt(POSITION, 0)
        initUI()
        mBinding?.apply {
            rvDev.adapter = DeveloperSettingAdapter(createData(), onClickItem = {
                when (it) {
                    RATE -> {
                        showDialogRate()
                    }

                    FEEDBACK -> {
                        feedBack()
                    }

                    SHARE -> {
                        Toast.makeText(requireContext(), "Coming soon", Toast.LENGTH_SHORT).show()
                    }

                    PRIVACY -> {
                        Bridge.getInstance().openPrivacy(requireActivity())
                    }

                    TERMS -> {
                        Bridge.getInstance().openTerms(requireActivity())
                    }

                    ABOUT -> {
                        AboutDialogFragment().show(
                            requireActivity().supportFragmentManager,
                            AboutDialogFragment.ABOUT
                        )
                    }
                }
            })
            layoutVibrateSetting.apply {
                swVibrate.setOnSingleClickListener {
                    if (swVibrate.isChecked) {
                        editConfig.putBoolean(SAVE_VIBRATE, true).apply()
                    } else {
                        editConfig.putBoolean(SAVE_VIBRATE, false).apply()
                    }
                }
            }

            layoutSoundSetting.apply {
                swSound.setOnSingleClickListener {
                    if (swSound.isChecked) {
                        editConfig.putBoolean(SAVE_SOUND, true).apply()
                    } else {
                        editConfig.putBoolean(SAVE_SOUND, false).apply()
                    }
                }
            }

            layoutBannerSetting.setOnSingleClickListener {
                if (!sharedPreferences.getBoolean(TryFreeDialogFragment.TRY_FREE, false)) {
                    TryFreeDialogFragment().show(
                        requireActivity().supportFragmentManager,
                        TryFreeDialogFragment.TRY_FREE
                    )
                } else {
                    Toast.makeText(requireContext(), "You using premium", Toast.LENGTH_SHORT).show()
                }
            }

            layoutSerachSetting.layout.setOnSingleClickListener {
                showDialogSearch()
            }


        }
    }

    private fun feedBack() {
        val emailAddress = "eyestormpteltd@gmail.com"
        val emailIntent = Intent(Intent.ACTION_SENDTO)
        emailIntent.data = Uri.parse("mailto:$emailAddress")
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject of the email")
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Body of the email")
        if (activity?.let { emailIntent.resolveActivity(it.packageManager) } != null) {
            startActivity(emailIntent)
        }

    }

    private fun shareApp() {

    }

    private fun initUI() {
        mBinding?.apply {
            layoutVibrateSetting.swVibrate.isChecked = isVibrate
            layoutSoundSetting.swSound.isChecked = isSound
        }
    }

    private fun showDialogRate() {
        val dialog = Dialog(requireContext())
        dialog.setCancelable(true)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_rate)
        val cancel = dialog.findViewById<ImageView>(R.id.ivCancel)
        cancel.setOnSingleClickListener {
            dialog.dismiss()
        }
        val window = dialog.window
        window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            val windowAttribute = attributes
            windowAttribute.gravity = Gravity.CENTER
            attributes = windowAttribute
        }
        dialog.show()
    }

    @SuppressLint("InflateParams")
    private fun showDialogSearch() {
        val dialog = Dialog(requireContext())
        dialog.setCancelable(true)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_search_engine)
        val window = dialog.window
        window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            val windowAttribute = attributes
            windowAttribute.gravity = Gravity.CENTER
            attributes = windowAttribute
        }

        val recyclerView = dialog.findViewById<RecyclerView>(R.id.rvSearch)
        recyclerView.adapter =
            SearchEngineAdapter(
                requireContext(),
                createListSearch(),
                positionAdapter,
                getItemClick = { data, position ->
                    positionAdapter = position
                    editConfig.putInt(
                        POSITION,
                        position
                    ).apply()
                    when (data.nameSearch) {
                        GOOGLE -> {
                            editConfig.putString(
                                SAVE_SEARCH_ENGINE,
                                "http://www.google.com/search?q="
                            ).apply()
                        }

                        YAHOO -> {
                            editConfig.putString(
                                SAVE_SEARCH_ENGINE,
                                "https://search.yahoo.com/search?q="
                            ).apply()
                        }

                        DUCKDUCKGO -> {
                            editConfig.putString(
                                SAVE_SEARCH_ENGINE,
                                "https://duckduckgo.com/?va=q&t=he&q="
                            ).apply()
                        }

                        YANDEX -> {
                            editConfig.putString(
                                SAVE_SEARCH_ENGINE,
                                "https://yandex.com/search/?text="
                            ).apply()
                        }

                        ECOSIA -> {
                            editConfig.putString(
                                SAVE_SEARCH_ENGINE,
                                "https://www.ecosia.org/search?method=index&q="
                            ).apply()
                        }

                        else -> {
                            editConfig.putString(
                                SAVE_SEARCH_ENGINE,
                                "https://www.bing.com/search?q="
                            ).apply()
                        }
                    }
                })
        dialog.show()
    }

    private fun createListSearch(): List<SearchEngineModel> {
        return arrayListOf(
            SearchEngineModel(
                requireContext().getString(R.string.google)
            ),
            SearchEngineModel(
                requireContext().getString(
                    R.string.bing
                )
            ),
            SearchEngineModel(
                requireContext().getString(R.string.yahoo)
            ),
            SearchEngineModel(
                requireContext().getString(R.string.DuckDuckGo)
            ),
            SearchEngineModel(
                requireContext().getString(R.string.Ecosia)
            ),
            SearchEngineModel(
                requireContext().getString(R.string.Yandex)
            )
        )
    }

    private fun createData(): List<DeveloperSetting> {
        val list = ArrayList<DeveloperSetting>()
        list.add(
            DeveloperSetting(
                this.resources.getDrawable(R.drawable.rate_setting),
                this.getString(R.string.Rate_Us_Stars),
                this.resources.getDrawable(R.drawable.arrow_setting)
            )
        )
        list.add(
            DeveloperSetting(
                this.resources.getDrawable(R.drawable.feeback_setting),
                this.getString(R.string.Feedback),
                this.resources.getDrawable(R.drawable.arrow_setting)
            )
        )
        list.add(
            DeveloperSetting(
                this.resources.getDrawable(R.drawable.share_setting),
                this.getString(R.string.Share_Our_App),
                this.resources.getDrawable(R.drawable.arrow_setting)
            )
        )
        list.add(
            DeveloperSetting(
                this.resources.getDrawable(R.drawable.privacy_policy_setting),
                this.getString(R.string.Privacy_Policy),
                this.resources.getDrawable(R.drawable.arrow_setting)
            )
        )
        list.add(
            DeveloperSetting(
                this.resources.getDrawable(R.drawable.terms_setting),
                this.getString(R.string.Terms_of_Use),
                this.resources.getDrawable(R.drawable.arrow_setting)
            )
        )
        list.add(
            DeveloperSetting(
                this.resources.getDrawable(R.drawable.about_setting),
                this.getString(R.string.About),
                this.resources.getDrawable(R.drawable.arrow_setting)
            )
        )
        return list
    }


}