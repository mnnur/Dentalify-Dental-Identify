package com.ps108.dentify.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import com.ps108.dentify.BuildConfig
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val FILENAME_FORMAT = "yyyyMMdd_HHmmss"
private val timeStamp: String = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(Date())
private const val MAXIMAL_SIZE = 1000000

fun getImageUri(context: Context): Uri {
    var uri: Uri? = null
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "$timeStamp.jpg")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/MyCamera/")
        }
        uri = context.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )
    }
    return uri ?: getImageUriForPreQ(context)
}

private fun getImageUriForPreQ(context: Context): Uri {
    val filesDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    val imageFile = File(filesDir, "/MyCamera/$timeStamp.jpg")
    if (imageFile.parentFile?.exists() == false) imageFile.parentFile?.mkdir()
    return FileProvider.getUriForFile(
        context,
        "${BuildConfig.APPLICATION_ID}.fileprovider",
        imageFile
    )
}

fun createCustomTempFile(context: Context): File {
    val filesDir = context.externalCacheDir
    return File.createTempFile(timeStamp, ".jpg", filesDir)
}

fun uriToFile(imageUri: Uri, context: Context): File {
    val myFile = createCustomTempFile(context)
    val inputStream = context.contentResolver.openInputStream(imageUri) as InputStream
    val outputStream = FileOutputStream(myFile)
    val buffer = ByteArray(1024)
    var length: Int
    while (inputStream.read(buffer).also { length = it } > 0) outputStream.write(buffer, 0, length)
    outputStream.close()
    inputStream.close()
    return myFile
}

fun File.reduceFileImage(): File {
    val file = this
    val bitmap = BitmapFactory.decodeFile(file.path).getRotatedBitmap(file)
    var compressQuality = 100
    var streamLength: Int
    do {
        val bmpStream = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
        val bmpPicByteArray = bmpStream.toByteArray()
        streamLength = bmpPicByteArray.size
        compressQuality -= 5
    } while (streamLength > MAXIMAL_SIZE)
    bitmap?.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))
    return file
}

fun Bitmap.getRotatedBitmap(file: File): Bitmap? {
    val orientation = ExifInterface(file).getAttributeInt(
        ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED
    )
    return when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(this, 90F)
        ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(this, 180F)
        ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(this, 270F)
        ExifInterface.ORIENTATION_NORMAL -> this
        else -> this
    }
}

fun rotateImage(source: Bitmap, angle: Float): Bitmap {
    val matrix = Matrix()
    matrix.postRotate(angle)
    return Bitmap.createBitmap(
        source, 0, 0, source.width, source.height, matrix, true
    )
}

fun getCurrentDateTime(format : String): String {
    val dateFormat = SimpleDateFormat(format, Locale.getDefault())
    val date = Date()
    return dateFormat.format(date)
}

fun titleSelector(disease : String) : String{
    val descriptionText = when (disease) {
        "Cas" -> "Canker Sores"
        "CoS" -> "Cold Sores"
        "Gum" -> "Gangivostomatitis"
        "MC" -> "Mouth Cancer"
        "OC" -> "Oral Cancer"
        "OLP" -> "Oral Lichen Planu"
        "OT" -> "Oral Thrush"
        "caries" -> "Caries"
        "gingivitis" -> "Gingivitis"
        "toothDiscoloration" -> "Tooth Discoloration"
        "ulcers" -> "Ulcers"
        "hypodontia" -> "Hypodontia"
        "calculus" -> "Calculus (Karang Gigi)"
        else -> "Selamat anda sehat"
    }
    return descriptionText
}

fun descriptionSelector(disease : String) : String{
    val descriptionText = when (disease) {
        "Cas" -> "Lebih dikenal masyarakat umum sebagai sariawan, jenis sariawan berupa luka kecil seperti lepuhan yang muncul di jaringan lunak dalam mulut dan bisa muncul di bagian dalam mulut, lidah ataupun gusi.\n" +
                "Saran : \n" +
                "Menjaga kesehatan dan kebersihan gigi dan mulut.\n" +
                "Mengonsumsi makanan yang mengandung vitamin B12, asam folat, dan zat besi.\n" +
                "Berhati-hatilah ketika Anda mengunyah makanan atau menyikat gigi supaya jaringan lunak dalam mulut tidak terluka. Gunakan sikat gigi yang sesuai dengan kebutuhan Anda, misalnya dengan bulu sikat yang lembut.\n" +
                "Banyak minum air putih.\n" +
                "Hindari kontak fisik dengan orang yang sedang menderita cold sores.\n" +
                "Cuci tangan Anda secara berkala.\n" +
                "Sumber : https://www.tanyapepsodent.com/tips-kesehatan-gigi/kesehatan-gusi-dan-mulut/apa-itu-canker-sore-dan-empat-cara-mengobati-sariawan.html\n"
        "CoS" -> "Gejala herpes di bibir dan mulut biasanya muncul dalam kurun waktu 1–3 minggu setelah terinfeksi virus (masa inkubasi virus). Gejala yang muncul pun beragam, tetapi penderita herpes di bibir atau mulut biasanya akan mengalami sariawan setelah melakukan kontak dengan penderita herpes oral.\n" +
                "Saran : \n" +
                "Mengonsumsi obat pereda nyeri, seperti paracetamol\n" +
                "Menjaga kondisi bibir dan mulut tetap bersih\n" +
                "Mengompres area yang luka dengan kompres dingin atau hangat untuk meredakan rasa sakit yang muncul\n" +
                "Menghindari konsumsi minuman hangat, makanan pedas, asam dan asin selama beberapa waktu\n" +
                "Sumber : https://www.alodokter.com/mengenali-herpes-di-bibir-dan-mulut-dan-cara-mengatasinya\n"
        "Gum" -> "penyakit infeksi yang terjadi pada mulut dan gigi. Biasanya, kondisi ini terjadi ketika ada infeksi virus atau bakteri. Infeksi pada mulut tersebut bisa menyebabkan munculnya luka, lenting, dan sariawan pada mulut. Luka dan sariawan bisa terbentuk di lidah, bawah lidah, bagian dalam pipi, serta bibir dan gusi.\n" +
                "\n" +
                "Saran : \n" +
                "Kumur dengan air hangat yang dicampur dengan 1/2 sendok teh garam, atau obat kumur dengan kandungan hidrogen peroksida sebanyak 2 kali sehari.\n" +
                "Konsumsi makanan yang sehat. Untuk sementara waktu, hindari makanan yang terlalu pedas, asin, atau asam. Makanan tersebut dapat memperparah atau mengiritasi luka di mulut.\n" +
                "Selalu jaga kebersihan mulut dan gigi dengan sikat gigi dan flossing setiap hari.\n" +
                "Sumber : https://hellosehat.com/gigi-mulut/gusi-mulut/gingivostomatitis/\n"
        "MC" -> "Terjadi ketika jaringan di dalam mulut tumbuh secara tidak normal. Hal ini disebabkan oleh perubahan atau mutasi genetik pada sel-sel di jaringan tersebut. Meski begitu, penyebab mutasi genetik ini sendiri belum diketahui dengan pasti.\n" +
                "Saran :\n" +
                "Gejala awal kanker mulut, seperti sariawan, sering kali dianggap sebagai kondisi yang tidak berbahaya dan diabaikan sampai kondisinya sudah parah. Waspadai gejala kanker mulut di atas dan segera periksakan diri ke dokter gigi jika keluhan tersebut tidak kunjung sembuh selama lebih dari 2 minggu.\n" +
                "Sumber : https://www.alodokter.com/kanker-mulut\n"
        "OC" -> " Kanker mulut adalah kanker yang terjadi pada jaringan dinding mulut, bibir, lidah, gusi, atau langit-langit. Kanker mulut juga dapat menyerang jaringan di tenggorokan (faring) dan kelenjar ludah.\n" +
                "Saran: Metode penanganannya adalah dengan operasi, kemoterapi, radioterapi, dan terapi target. Keempat metode pengobatan ini dapat dikombinasikan guna mendapatkan hasil yang maksimal.\n" +
                "(https://www.alodokter.com/kanker-mulut)\n"
        "OLP" -> "Penyakit mukokutan autoimun yang banyak terjadi pada wanita periode menopause. Penting untuk mengetahui stres yang menjadi faktor etiologi dari lesi OLP pada wanita yang memasuki periode menopause.\n" +
                "Saran :\n" +
                "Perawatan terutama ditujukan untuk mengendalikan gejala dan berfokus pada memfasilitasi penyembuhan lesi yang parah dan mengurangi rasa sakit atau ketidaknyamanan lainnya.\n" +
                "Sumber : https://www.kin.es/id/patologias/liquen-plano-oral/\n"
        "OT" -> "Penyakit yang terjadi akibat adanya penumpukan jamur Candida albicans (C. albicans) di lapisan mulut.\n" +
                "Saran :\n" +
                "Orang dewasa dan anak-anak yang sehat. Untuk kelompok ini, dokter mungkin akan merekomendasikan obat antijamur. Obat tersebut bisa tersedia dalam beberapa bentuk, termasuk pelega tenggorokan, tablet, atau cairan yang dikonsumsi secara oral. \n" +
                "Ibu juga bisa coba baca Perawatan yang Tepat saat Anak Mengalami Oral Thrush. \n" +
                "Bayi dan ibu menyusui. Bila ibu sedang menyusui dan bayi ibu mengidap kandidiasis mulut, ibu dan bayi bisa menularkan infeksi tersebut bolak-balik. Dokter mungkin akan meresepkan obat antijamur ringan untuk bayi dan krim antijamur untuk payudara ibu.\n" +
                "Orang dewasa dengan sistem kekebalan yang lemah. Paling sering dokter akan merekomendasikan obat antijamur.\n" +
                "Sumber : https://www.halodoc.com/kesehatan/oral-thrush\n"
        "caries" -> "Karies gigi adalah kondisi ketika lapisan struktur gigi mengalami kerusakan secara bertahap. Kondisi ini terjadi ketika bakteri yang melekat di gigi, terutama Streptococcus mutans, menghasilkan asam dari sisa-sisa makanan seperti karbohidrat. Apabila kondisi ini dibiarkan akan menyebabkan terjadinya gigi berlubang.\n" +
                "Saran: Ada empat cara utama untuk mengatasi karies gigi. Perawatan yang dilakukan oleh dokter gigi ini dapat membantu mengobati kerusakan akibat masalah gigi tersebut.\n" +
                "Tambalan: Dokter gigi akan mengebor area gigi yang terkena, membuang bahan yang membusuk di dalam rongga, dan menambal ruang kosong tersebut dengan bahan pengisi gigi yang sesuai. \n" +
                "Mahkota: Mahkota bisa menjadi pilihan lain untuk mengatasi karies gigi. Pilihan ini biasanya baru dilakukan ketika sebagian besar gigi sudah hancur akibat masalah gigi tersebut. \n" +
                "Saluran akar: Dokter gigi akan melakukan perawatan saluran akar dengan mengangkat saraf yang rusak atau mati dengan jaringan pembuluh darah (pulpa) di sekitarnya dan mengisi area tersebut. Biasanya diakhiri dengan menempatkan mahkota di atas area yang terkena.\n" +
                "Pencabutan: Dalam beberapa kasus, gigi mungkin rusak tidak dapat diperbaiki dan harus dicabut jika ada risiko infeksi menyebar ke tulang rahang.\n" +
                "(https://www.halodoc.com/kesehatan/karies-gigi)\n"
        "gingivitis" -> "Gingivitis alias radang gusi adalah kondisi yang terjadi karena ada peradangan pada gusi yang ditandai dengan bengkak serta kemerahan pada gusi di sekitar pangkal gigi. Kondisi ini bisa muncul karena menumpuknya sisa makanan di gigi dan gusi kemudian mengeras dan berubah menjadi plak.\n" +
                "Saran: Ada beberapa metode pengobatan gingivitis yang bisa dilakukan, antara lain scaling alias pembersihan karang gigi dan penambalan atau mengganti gigi yang rusak. Selain pengobatan, perawatan yang tepat juga harus dilakukan untuk membantu pemulihan serta mencegah radang gusi berulang. Untuk itu, biasakan selalu menyikat gigi setiap hari, setidaknya 2 kali sehari.\n" +
                "(https://www.halodoc.com/kesehatan/gingivitis)\n"
        "toothDiscoloration" -> "Tooth discoloration atau diskolorasi gigi adalah suatu kondisi di mana gigi Anda mengalami perubahan warna. Gigi yang awalnya berwarna putih mulai terlihat tidak secerah dulu menjadi lebih gelap atau terdapat bintik-bintik seperti noda di gigi. \n" +
                "Saran:\n" +
                "Menghindari makanan atau minuman yang bisa menyebabkan perubahan warna, seperti teh, kopi, atau alkohol. \n" +
                "Menggunakan produk pemutih yang dijual bebas dalam bentuk strip tempelan.\n" +
                "Membersihkan gigi setiap hari dengan teknik menggosok gigi dan flossing yang benar.\n" +
                "(https://www.ai-care.id/healthpedia-penyakit/tooth-discoloration)\n"
        "ulcers" -> "Ulcer merupakan suatu kelainan yang paling sering dijumpai pada  mukosa rongga mulut. Ulcer rongga mulut dapat disebabkan infeksi bakteri,  infeksi virus, reaksi hipersensitivitas, keganasan, manifestasi oral dari  penyakit sistemik atau penyakit kulit, trauma mekanis, termal, atau kimia.\n" +
                "Saran: \n" +
                "Jenis sariawan lainnya atau yang disebabkan oleh virus atau Jamur, memerlukan pengobatan topikal antivirus atau antijamur. Dokter akan menyarankan terapi sebagai pengganti vitamin, gizi, dan elektrolit yang kurang di dalam tubuh seperti:\n" +
                "cairan dan elektrolit,\n" +
                "zat besi,\n" +
                "asam folat, dan\n" +
                "vitamin B12.\n" +
                "Sumber: https://repository.unair.ac.id/101463/4/4.%20BAB%20I%20PENDAHULUAN%20.pdf\n" +
                "\n"
        "hypodontia" -> "Hypodontia adalah kelainan genetik pada gigi ketika terdapat satu atau lebih gigi yang tidak tumbuh sama sekali. Kondisi ini disebabkan oleh kelainan genetik yang tidak dapat dicegah dan ada lebih dari 10 gen yang berperan dalam memunculkan kelainan ini.\n" +
                "Saran: Penanganan terhadap hypodontia bertujuan untuk memperbaiki penampilan Anda dan memulihkan kemampuan makan, mengunyah, maupun berbicara yang sempat terganggu. Beberapa penanganan yang umum dilakukan terhadap penderita hypodontia:\n" +
                "Pemasangan kawat gigi\n" +
                "Mengatur ulang bentuk gigi\n" +
                "Menggunakan gigi palsu\n" +
                "(https://hellosehat.com/gigi-mulut/gusi-mulut/hypodontia-adalah/)\n" +
                "\n"
        "calculus" -> " Karang gigi adalah lapisan seperti kotoran yang mengeras di gigi. Karang gigi sulit dihilangkan meski telah dibersihkan atau disikat berulang kali.Karang gigi disebabkan oleh adanya plak pada gigi yang tidak dihilangkan. Plak gigi itu sendiri merupakan lapisan licin dan tipis yang terbentuk dari sisa-sisa makanan yang tertinggal di gigi.\n" +
                "Saran: Plak yang sudah mengeras dan menjadi karang gigi tidak bisa dihilangkan hanya dengan menggosok gigi. Untuk mengatasi kondisi tersebut, dokter akan menganjurkan pasien untuk menjalani scaling gigi. Akan tetapi, karang gigi dapat dicegah dengan cara berikut:\n" +
                "Menyikat gigi setidaknya dua kali sehari dengan pasta gigi yang mengandung fluoride\n" +
                "Membersihkan gigi dengan benang gigi setidaknya satu kali sehari\n" +
                "Menggunakan obat kumur\n" +
                "Mengonsumsi makanan bergizi lengkap dan seimbang\n" +
                "Tidak merokok\n" +
                "(https://www.alodokter.com/karang-gigi)\n"
        else -> "Selamat anda sehat"
    }
    return descriptionText
}