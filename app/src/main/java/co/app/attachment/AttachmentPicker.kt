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

package co.app.attachment

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.annotation.LongDef
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.exifinterface.media.ExifInterface
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.app.R
import co.app.common.Attachment
import co.app.common.Photo
import co.app.domain.Settings
import co.app.dsl.listItems
import co.app.dsl.startActivity
import co.app.photo.CameraXActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.io.ByteArrayOutputStream
import java.io.IOException

class AttachmentPicker : BottomSheetDialogFragment() {
    private lateinit var settings: Settings
    private var uri: Uri? = null
    private var fileName = ""
    var onPickerCloseListener: ((Attachment) -> Unit?)? = null

    private var dialogTitle = ""
    private var dialogTitleId = 0
    private var dialogTitleSize = 0F
    private var dialogTitleColor = 0

    @ListType
    private var dialogListType: Long = TYPE_LIST
    private var dialogGridSpan = 3
    private var dialogItems = ArrayList<AttachmentModel>()

    @DialogStyle
    private var dialogStyle = DIALOG_STANDARD

    companion object {
        private const val ARG_TITLE = "title"
        private const val ARG_TITLE_ID = "titleId"
        private const val ARG_TITLE_SIZE = "titleSize"
        private const val ARG_TITLE_COLOR = "titleColor"
        private const val ARG_LIST_TYPE = "list"
        private const val ARG_GRID_SPAN = "gridSpan"
        private const val ARG_ITEMS = "items"

        const val REQUEST_PERMISSION_CAMERA = 1001
        const val REQUEST_PERMISSION_GALLERY = 1002
        const val REQUEST_PERMISSION_VIDEO = 1003
        const val REQUEST_PERMISSION_VIDEO_GALLERY = 1004
        const val REQUEST_PERMISSION_FILE = 1005
        const val REQUEST_TAKE_PHOTO = 1101
        const val REQUEST_PICK_PHOTO = 1102
        const val REQUEST_VIDEO = 1103
        const val REQUEST_PICK_FILE = 1104

        private fun newInstance(
            dialogTitle: String,
            dialogTitleId: Int,
            dialogTitleSize: Float,
            dialogTitleColor: Int,
            dialogListType: Long,
            dialogGridSpan: Int,
            dialogItems: ArrayList<AttachmentModel>
        ): AttachmentPicker {

            val args = Bundle()

            args.putString(ARG_TITLE, dialogTitle)
            args.putInt(ARG_TITLE_ID, dialogTitleId)
            args.putFloat(ARG_TITLE_SIZE, dialogTitleSize)
            args.putInt(ARG_TITLE_COLOR, dialogTitleColor)
            args.putLong(ARG_LIST_TYPE, dialogListType)
            args.putInt(ARG_GRID_SPAN, dialogGridSpan)
            args.putParcelableArrayList(ARG_ITEMS, dialogItems)

            val dialog = AttachmentPicker()
            dialog.arguments = args
            return dialog
        }

        @LongDef(
            TYPE_LIST,
            TYPE_GRID
        )
        @Retention(AnnotationRetention.SOURCE)
        annotation class ListType

        const val TYPE_LIST = 0L
        const val TYPE_GRID = 1L

        @LongDef(
            DIALOG_STANDARD,
            DIALOG_MATERIAL
        )
        @Retention(AnnotationRetention.SOURCE)
        annotation class DialogStyle

        const val DIALOG_STANDARD = 10L
        const val DIALOG_MATERIAL = 20L
    }

    class Builder() {

        private var dialogTitle = ""
        private var dialogTitleId = 0
        private var dialogTitleSize = 0F
        private var dialogTitleColor = 0

        @ListType
        private var dialogListType = TYPE_LIST
        private var dialogGridSpan = 3
        private var dialogItems = ArrayList<AttachmentModel>()

        @DialogStyle
        private var dialogStl = DIALOG_STANDARD

        fun setTitle(title: String): Builder {
            dialogTitle = title
            return this
        }

        fun setTitle(title: Int): Builder {
            dialogTitleId = title
            return this
        }

        fun setTitleTextSize(textSize: Float): Builder {
            dialogTitleSize = textSize
            return this
        }

        fun setTitleTextColor(textColor: Int): Builder {
            dialogTitleColor = textColor
            return this
        }

        fun setListType(@ListType type: Long, gridSpan: Int = 3): Builder {
            dialogListType = type
            dialogGridSpan = gridSpan
            return this
        }

        fun setItems(items: ArrayList<AttachmentModel>): Builder {
            items.forEachIndexed { i, itemModel ->
                items.forEachIndexed { j, itemModel2 ->
                    if (i != j && itemModel2.type == itemModel.type)
                        throw IllegalStateException("You cannot have two similar item models in this list")
                }
            }
            dialogItems = items
            return this
        }

        fun setDialogStyle(@DialogStyle style: Long): Builder {
            dialogStl = style
            return this
        }

        fun create(): AttachmentPicker {
            val dialog =
                newInstance(
                    dialogTitle,
                    dialogTitleId,
                    dialogTitleSize,
                    dialogTitleColor,
                    dialogListType,
                    dialogGridSpan,
                    dialogItems
                )

            dialog.dialogStyle = dialogStl
            return dialog
        }
    }

    override fun getTheme() = if (dialogStyle == DIALOG_MATERIAL)
        R.style.Theme_MaterialComponents_BottomSheetDialog
    else R.style.Animation_Design_BottomSheetDialog


    override fun onCreateDialog(savedInstanceState: Bundle?) =
        BottomSheetDialog(requireContext(), theme)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.bottom_sheet_attachment_picker, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getData()
        createTitle(view)
        createList(view)
    }

    private fun getData() {
        val args = arguments ?: return

        dialogTitle = args.getString(ARG_TITLE) ?: "Pick Photo"
        dialogTitleId = args.getInt(ARG_TITLE_ID)
        dialogTitleSize = args.getFloat(ARG_TITLE_SIZE)
        dialogTitleColor = args.getInt(ARG_TITLE_COLOR)
        dialogListType = args.getLong(ARG_LIST_TYPE)
        dialogGridSpan = args.getInt(ARG_GRID_SPAN)
        dialogItems = args.getParcelableArrayList(ARG_ITEMS)!!
    }

    private fun createTitle(view: View) {
        val pickerTitle = view.findViewById<TextView>(R.id.pickerTitle)
        if (dialogTitle == "" && dialogTitleId == 0) {
            pickerTitle.visibility = View.GONE
            return
        }
        if (dialogTitle == "") pickerTitle.setText(dialogTitleId) else pickerTitle.text =
            dialogTitle
        if (dialogTitleSize != 0F) pickerTitle.textSize = dialogTitleSize
        pickerTitle.setTextColor(
            if (dialogTitleColor == 0) ContextCompat.getColor(context!!, R.color.color_on_surface)
            else dialogTitleColor
        )
    }

    private fun createList(view: View) {
        val viewItem =
            if (dialogListType == TYPE_LIST) R.layout.attachment_list_item else R.layout.attachment_grid_item
        val manager = if (dialogListType == TYPE_LIST)
            LinearLayoutManager(context) else GridLayoutManager(context, dialogGridSpan)
        val pickerItems = view.findViewById<RecyclerView>(R.id.pickerItems)
        pickerItems.listItems(
            dialogItems,
            viewItem,
            manager,
            { v: View, item: AttachmentModel, _: Int ->
                initIconBackground(item, v)
                initIcon(item, v.findViewById(R.id.icon))
                initLabel(item, v.findViewById(R.id.label))
            },
            { item: AttachmentModel, _: Int ->
                when (item.type) {
                    AttachmentModel.ITEM_CAMERA -> {
                        if (ActivityCompat.checkSelfPermission(
                                context!!,
                                Manifest.permission.CAMERA
                            )
                            != PackageManager.PERMISSION_GRANTED
                            || ActivityCompat.checkSelfPermission(
                                context!!,
                                Manifest.permission.READ_EXTERNAL_STORAGE
                            )
                            != PackageManager.PERMISSION_GRANTED
                            || ActivityCompat.checkSelfPermission(
                                context!!,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                            )
                            != PackageManager.PERMISSION_GRANTED
                        ) {
                            ActivityCompat.requestPermissions(
                                activity!!,
                                arrayOf(
                                    Manifest.permission.CAMERA,
                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                                ),
                                REQUEST_PERMISSION_CAMERA
                            )
                        } else {
                            openCamera()
                        }
                    }
                    AttachmentModel.ITEM_GALLERY -> {
                        if (ActivityCompat.checkSelfPermission(
                                context!!,
                                Manifest.permission.READ_EXTERNAL_STORAGE
                            )
                            != PackageManager.PERMISSION_GRANTED
                            || ActivityCompat.checkSelfPermission(
                                context!!,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                            )
                            != PackageManager.PERMISSION_GRANTED
                        ) {
                            ActivityCompat.requestPermissions(
                                activity!!,
                                arrayOf(
                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                                ),
                                REQUEST_PERMISSION_GALLERY
                            )
                        } else {
                            openGallery()
                        }
                    }
                    AttachmentModel.ITEM_VIDEO -> {
                        if (ActivityCompat.checkSelfPermission(
                                context!!,
                                Manifest.permission.CAMERA
                            )
                            != PackageManager.PERMISSION_GRANTED
                            || ActivityCompat.checkSelfPermission(
                                context!!,
                                Manifest.permission.READ_EXTERNAL_STORAGE
                            )
                            != PackageManager.PERMISSION_GRANTED
                            || ActivityCompat.checkSelfPermission(
                                context!!,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                            )
                            != PackageManager.PERMISSION_GRANTED
                        ) {
                            ActivityCompat.requestPermissions(
                                activity!!,
                                arrayOf(
                                    Manifest.permission.CAMERA,
                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                                ),
                                REQUEST_PERMISSION_VIDEO
                            )
                        } else {
                            openVideoCamera()
                        }
                    }

                    AttachmentModel.ITEM_FILES -> {
                        if (ActivityCompat.checkSelfPermission(
                                context!!,
                                Manifest.permission.READ_EXTERNAL_STORAGE
                            )
                            != PackageManager.PERMISSION_GRANTED
                            || ActivityCompat.checkSelfPermission(
                                context!!,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                            )
                            != PackageManager.PERMISSION_GRANTED
                        ) {
                            ActivityCompat.requestPermissions(
                                activity!!,
                                arrayOf(
                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                                ),
                                REQUEST_PERMISSION_FILE
                            )
                        } else {
                            openFilePicker()
                        }
                    }
                }
            }
        )
    }

    private fun initIconBackground(item: AttachmentModel, view: View) {
        if (item.hasBackground) {
            val color = if (item.itemBackgroundColor == 0)
                ContextCompat.getColor(view.context, R.color.color_secondary)
            else item.itemBackgroundColor

            val bg: Drawable?

            when (item.backgroundType) {
                AttachmentModel.TYPE_SQUARE -> {
                    bg = ContextCompat.getDrawable(view.context, R.drawable.bg_square)
                    bg?.mutate()?.setColorFilter(color, PorterDuff.Mode.SRC_IN)
                }
                AttachmentModel.TYPE_ROUNDED_SQUARE -> {
                    bg = ContextCompat.getDrawable(view.context, R.drawable.bg_rounded_square)
                    bg?.mutate()?.setColorFilter(color, PorterDuff.Mode.SRC_IN)
                }
                else -> {
                    bg = ContextCompat.getDrawable(view.context, R.drawable.bg_circle)
                    bg?.mutate()?.setColorFilter(color, PorterDuff.Mode.SRC_IN)
                }
            }

            view.background = bg
        }
    }

    private fun initIcon(item: AttachmentModel, icon: AppCompatImageView) =
        if (item.itemIcon == 0) icon.setImageResource(
            when (item.type) {
                AttachmentModel.ITEM_GALLERY -> R.drawable.ic_image
                AttachmentModel.ITEM_VIDEO -> R.drawable.ic_videocam
                AttachmentModel.ITEM_FILES -> R.drawable.ic_file
                else -> R.drawable.ic_camera
            }
        ) else icon.setImageResource(item.itemIcon)

    private fun initLabel(item: AttachmentModel, label: AppCompatTextView) =
        if (item.itemLabel == "") label.setText(
            when (item.type) {
                AttachmentModel.ITEM_GALLERY -> R.string.gallery
                AttachmentModel.ITEM_VIDEO -> R.string.video
                AttachmentModel.ITEM_FILES -> R.string.document
                else -> R.string.camera
            }
        )
        else label.text = item.itemLabel

    fun onPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_PERMISSION_CAMERA -> if (grantResults.isNotEmpty()
                && grantResults.first() == PackageManager.PERMISSION_GRANTED
            ) {
                openCamera()
            }
            REQUEST_PERMISSION_GALLERY -> if (grantResults.isNotEmpty()
                && grantResults.first() == PackageManager.PERMISSION_GRANTED
            ) {
                openGallery()
            }
            REQUEST_PERMISSION_VIDEO -> if (grantResults.isNotEmpty()
                && grantResults.first() == PackageManager.PERMISSION_GRANTED
            ) {
                openVideoCamera()
            }
            REQUEST_PERMISSION_VIDEO_GALLERY -> if (grantResults.isNotEmpty()
                && grantResults.first() == PackageManager.PERMISSION_GRANTED
            ) {
                openVideoGallery()
            }
            REQUEST_PERMISSION_FILE -> if (grantResults.isNotEmpty()
                && grantResults.first() == PackageManager.PERMISSION_GRANTED
            ) {
                openFilePicker()
            }
        }
    }

    private fun openCamera() {
        if (settings.useAppCamera) requireContext().startActivity<CameraXActivity>()
        else {
            fileName = (System.currentTimeMillis() / 1000).toString() + ".jpg"
            val contentValues = ContentValues()
            contentValues.put(MediaStore.Images.Media.TITLE, fileName)
            contentValues.put(MediaStore.Images.Media.DESCRIPTION, getString(R.string.app_name))
            uri = context!!.contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )
            val takePhoto = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            takePhoto.putExtra(MediaStore.EXTRA_OUTPUT, uri)
            startActivityForResult(
                takePhoto,
                REQUEST_TAKE_PHOTO
            )
        }

    }

    private fun openGallery() {
        val pickPhoto = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(
            pickPhoto,
            REQUEST_PICK_PHOTO
        )
    }

    private fun openVideoCamera() {
        fileName = (System.currentTimeMillis() / 1000).toString() + ".mp4"

        val takeVideo = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        takeVideo.putExtra(
            MediaStore.EXTRA_OUTPUT,
            Environment.getExternalStorageDirectory().absolutePath + "/" + fileName
        )

        startActivityForResult(
            takeVideo,
            REQUEST_VIDEO
        )
    }

    private fun openVideoGallery() {
        val pickVideo = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(
            pickVideo,
            REQUEST_VIDEO
        )
    }

    private fun openFilePicker() {
        val pickFile = Intent(Intent.ACTION_GET_CONTENT)
        pickFile.type = "*/*"

        startActivityForResult(
            pickFile,
            REQUEST_PICK_FILE
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            when (requestCode) {
                REQUEST_TAKE_PHOTO -> takePhoto()
                REQUEST_PICK_PHOTO -> pickPhoto(data)
                REQUEST_VIDEO -> pickVideo(data)
                REQUEST_PICK_FILE -> pickFile(data)
            }
        }
    }

    private fun takePhoto() {
        val uri = this.uri ?: return

        var bitmap: Bitmap
        try {
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = false
            options.inPreferredConfig = Bitmap.Config.RGB_565
            options.inDither = true
            bitmap = BitmapFactory.decodeFile(uri.realPath(context!!).path, options)

            val exif = ExifInterface(uri.realPath(context!!).path!!)

            when (exif.getAttribute(ExifInterface.TAG_ORIENTATION)) {
                "6" -> bitmap = bitmap rotate 90
                "8" -> bitmap = bitmap rotate 270
                "3" -> bitmap = bitmap rotate 180
            }
            if (onPickerCloseListener != null) onPickerCloseListener?.invoke(
                Photo().offline().url(bitmap.toUri(context!!, fileName).path)
            )
        } catch (e: IOException) {
            e.printStackTrace()
        }

        dismiss()
    }

    private fun pickPhoto(data: Intent?) {
        if (data == null) return
        val uri = data.data ?: return
        if (onPickerCloseListener != null) onPickerCloseListener?.invoke(
            Photo().offline().url(uri.path)
        )
        dismiss()
    }

    private fun pickVideo(data: Intent?) {
        if (data == null) return
        val uri = data.data ?: return
        if (onPickerCloseListener != null) {
            // onPickerCloseListener?.invoke(AttachmentModel.ITEM_VIDEO_GALLERY, uri)
        }
        dismiss()
    }

    private fun pickFile(data: Intent?) {
        if (data == null) {
            return
        }

        val uri = data.data ?: return

        if (onPickerCloseListener != null) {
            //onPickerCloseListener?.invoke(AttachmentModel.ITEM_FILES, uri)
        }
        dismiss()
    }

    fun setPickerCloseListener(onClose: (Attachment) -> Unit) {
        onPickerCloseListener = onClose
    }

    @Deprecated(
        "use show pass settings",
        level = DeprecationLevel.ERROR,
        replaceWith = ReplaceWith(
            "show(manager: FragmentManager, settings: Settings, tag: String?)",
            "co.app.domain.Settings"
        )
    )
    override fun show(manager: FragmentManager, tag: String?) {
        throw IllegalAccessException("deprecated")
    }

    @Deprecated(
        "use show pass settings",
        level = DeprecationLevel.ERROR,
        replaceWith = ReplaceWith(
            "show(transaction: FragmentTransaction, settings: Settings, tag: String?)",
            "co.app.domain.Settings"
        )
    )
    override fun show(transaction: FragmentTransaction, tag: String?): Int {
        throw IllegalAccessException("deprecated")
    }

    @Deprecated(
        "use show pass settings",
        level = DeprecationLevel.ERROR,
        replaceWith = ReplaceWith(
            "showNow(manager: FragmentManager, settings: Settings, tag: String?)",
            "co.app.domain.Settings"
        )
    )
    override fun showNow(manager: FragmentManager, tag: String?) {
        throw IllegalAccessException("deprecated")
    }


    fun show(manager: FragmentManager, settings: Settings, tag: String?) {
        this.settings = settings
        super.show(manager, tag)
    }

    fun show(transaction: FragmentTransaction, settings: Settings, tag: String?) {
        this.settings = settings
        super.show(transaction, tag)
    }

    fun showNow(manager: FragmentManager, settings: Settings, tag: String?) {
        this.settings = settings
        super.showNow(manager, tag)
    }

}

internal fun Uri.realPath(context: Context): Uri {
    val result: String
    val cursor = context.contentResolver.query(this, null, null, null, null)

    if (cursor == null) {
        result = this.path!!
    } else {
        cursor.moveToFirst()
        val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
        result = cursor.getString(idx)
        cursor.close()
    }
    return Uri.parse(result)
}

internal fun Bitmap.toUri(context: Context, title: String): Uri {
    val bytes = ByteArrayOutputStream()
    compress(Bitmap.CompressFormat.JPEG, 100, bytes)
    val path = MediaStore.Images.Media.insertImage(
        context.contentResolver, this,
        title, context.getString(R.string.app_name)
    )
    return Uri.parse(path)
}

infix fun ViewGroup.inflate(@LayoutRes lyt: Int): View {
    return LayoutInflater.from(context).inflate(lyt, this, false)
}


infix fun Bitmap.rotate(degree: Int): Bitmap {
    val w = width
    val h = height
    val mtx = Matrix()
    mtx.postRotate(degree.toFloat())
    return Bitmap.createBitmap(this, 0, 0, w, h, mtx, true)
}
