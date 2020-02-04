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

package com.app

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_MODULE = "param1"
private const val ARG_DRAWABLE = "param2"
private const val ARG_MESSAGE = "param3"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [PlaceHolderModuleFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [PlaceHolderModuleFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PlaceHolderModuleFragment : BaseFragment() {
    // TODO: Rename and change types of parameters
    private var module: String? = null
    private var drwable: Int? = null
    private var message: String? = null
    private var listener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            module = it.getString(ARG_MODULE)
            drwable = it.getInt(ARG_DRAWABLE)
            message = it.getString(ARG_MESSAGE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =// Inflate the layout for this fragment
        inflater.inflate(R.layout.fragment_place_holder_module, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PlaceHolderModuleFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(module: String, drawable: Int, message: String) =
            PlaceHolderModuleFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_MODULE, module)
                    putInt(ARG_DRAWABLE, drawable)
                    putString(ARG_MESSAGE, message)
                }
            }
    }
}
