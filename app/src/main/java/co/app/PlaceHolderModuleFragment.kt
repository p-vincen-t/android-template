/*
 * Copyright 2020, {{App}}
 * Licensed under the Apache License, Version 2.0, "{{App}} Inc".
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package co.app

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringDef
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_place_holder_module.*
import promise.commons.makeInstance

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_MODULE = "arg_module"
private const val ARG_MESSAGE = "arg_message"
private const val ARG_FRAGMENT_CLASS = "arg_fragment_class"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [PlaceHolderModuleFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [PlaceHolderModuleFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PlaceHolderModuleFragment : BaseFragment() {
    private var module: String? = null
    private var fragmentClass: String? = null
    private var message: Int? = null
    private var listener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments == null) throw IllegalStateException("args must be present in fragment")
        arguments!!.let {
            module = it.getString(ARG_MODULE)
            message = it.getInt(ARG_MESSAGE)
            fragmentClass = it.getString(ARG_FRAGMENT_CLASS)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =// Inflate the layout for this fragment
        inflater.inflate(R.layout.fragment_place_holder_module, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (app.isModuleInstalled(module!!)) {
            view_switcher.showNext()
            val fragment = makeInstance(Class.forName(fragmentClass!!).kotlin) as Fragment
            childFragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit()
        } else {
            install_message_text_view.setText(message!!)
            install_button.setOnClickListener {
                onButtonPressed()
            }
        }
    }

    fun onButtonPressed() {
        listener?.onRequestedModule(module!!)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) listener = context
        else throw RuntimeException("$context must implement OnFragmentInteractionListener")
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        fun onRequestedModule(module: String)
    }

    companion object {

        const val REQUEST = "request"

        const val CATEGORIES_FRAGMENT_CLASS = "co.app.request.CategoriesFragment"


        @StringDef(
            CATEGORIES_FRAGMENT_CLASS
        )
        @Retention(AnnotationRetention.SOURCE)
        annotation class FragmentClass

        @StringDef(
            REQUEST
        )
        @Retention(AnnotationRetention.SOURCE)
        annotation class ModuleName

        @JvmStatic
        fun newInstance(@ModuleName module: String,
                        @FragmentClass fragmentClass: String,
                        @StringRes message: Int) =
            PlaceHolderModuleFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_MODULE, module)
                    putString(ARG_FRAGMENT_CLASS, fragmentClass)
                    putInt(ARG_MESSAGE, message)
                }
            }
    }
}
