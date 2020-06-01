package com.android.eventapplication.autocomplete.view

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.android.eventapplication.BaseActivity
import com.android.eventapplication.EventsApplication
import com.android.eventapplication.R
import com.android.eventapplication.autocomplete.model.preditictions.Prediction
import com.android.eventapplication.autocomplete.view_model.AutoCompleteViewModel
import javax.inject.Inject


class AutoCompleteFragment : Fragment(), AdapterView.OnItemClickListener, View.OnClickListener,
    TextWatcher {


    private lateinit var spinner: ProgressBar
    private lateinit var adapter: AutoCompleteAdapter
    private lateinit var apiKey: String
    private lateinit var parameters: String
    private lateinit var imageViewClear: ImageView
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: AutoCompleteViewModel
    private lateinit var autoCompleteTextView: AutoCompleteTextView
    private var mCallback: OnPlaceSelectedListner? = null
    private var isKeyDel: Boolean = false
    private var mContext: Context? = null
    private var previous: Int? = null

    companion object {
        private const val MIN_QUERY_LENGTH = 3
        private const val KEY_PARAM = "key="
        private const val INPUT_PARAM = "input="
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        (activity?.application as EventsApplication).appComponent.inject(this)
        viewModel = ViewModelProvider(this, viewModelFactory).get(AutoCompleteViewModel::class.java)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.auto_complete_fragment, container, false)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.clear()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        autoCompleteTextView = view.findViewById(R.id.autocompleteTextview)
        spinner = view.findViewById(R.id.progressBar)
        imageViewClear = view.findViewById(R.id.image_clear)
        imageViewClear.setOnClickListener(this)
        apiKey = "$KEY_PARAM${activity?.getString(R.string.google_maps_key)!!}"
        setUpAdapter()
        autoCompleteTextView.addTextChangedListener(this)


        viewModel.placeResponse.observe(viewLifecycleOwner, Observer { repos ->
            repos?.let {
                if (repos.predictions.isNotEmpty()) {
                    adapter.replaceData(it.predictions)
                } else {
                    (context as BaseActivity).showToast(repos.status)
                }

            }
        })

        // Show spinner when loading from API
        viewModel.spinner.observe(viewLifecycleOwner, Observer { value ->
            value?.let {
                if (it) {
                    (context as BaseActivity).showProgress(spinner)
                } else {
                    (context as BaseActivity).hideProgress(spinner)
                }


            }
        })


        // show the error message
        viewModel.errorMessage.observe(viewLifecycleOwner, Observer { text ->
            text?.let {
                (context as BaseActivity).showToast(text)
            }
        })


    }

    private fun setUpAdapter() {
        this.adapter =
            AutoCompleteAdapter(
                context = activity,
                layoutResource = android.R.layout.simple_list_item_1,
                predictionList = emptyList()
            )

        autoCompleteTextView.setAdapter(adapter)
        autoCompleteTextView.onItemClickListener = this

    }

    override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        val selectedItem: Prediction? = p0?.adapter?.getItem(p2) as Prediction?
        if (selectedItem != null) {
            autoCompleteTextView.setText(selectedItem.description)
            mCallback?.onPlaceSelected(selectedItem)
            activity?.supportFragmentManager?.popBackStack()
        }
        //        if (null != selectedItem?.place_id) {
//            autoCompleteTextView.setText(selectedItem.description)
//            val placeid = selectedItem.place_id
//            this.parameters = ""
//           this.parameters = "$PLACE_ID_PARAM$placeid&$apiKey"
//            this.viewModel.getPlaceDetails(parameters)
//        } else {
//            showToast(this.getString(R.string.error_select_valid_address))
//        }

    }

    override fun onClick(p0: View?) {
        if (p0 != null) {
            when (p0.id) {
                R.id.image_clear -> {
                    autoCompleteTextView.hideKeyboard()
                    autoCompleteTextView.setText("")
                }

            }
        }
    }

    fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        try {
            if (context is OnPlaceSelectedListner) {
                mCallback = context
            }

        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement OnPlaceSelectedListner")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mCallback = null
    }

    override fun afterTextChanged(p0: Editable?) {

    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        if (p0 != null) {
            previous = p0.length
        }
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        isKeyDel = previous!! > p0!!.length

        if (!p0.isNullOrEmpty()) {
            if (p0.length < MIN_QUERY_LENGTH) {
                return
            } else {
                if (!isKeyDel) {
                    autoCompleteTextView.hideKeyboard()
                    parameters = ""
                    parameters = "$INPUT_PARAM$p0&$apiKey"
                    if ((mContext as BaseActivity).isConnctedToNetwork()) {
                        viewModel.getAutoCompletePerditctions(parameters)
                    } else {
                        (context as BaseActivity).showToast(getString(R.string.error_code_offline))
                    }
                }
            }
        }
    }
}
