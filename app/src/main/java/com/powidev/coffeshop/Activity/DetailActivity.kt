package com.powidev.coffeshop.Activity

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.powidev.coffeshop.Domain.ItemsModel
import com.powidev.coffeshop.Helper.ManagmentCart
import com.powidev.coffeshop.R
import com.powidev.coffeshop.databinding.ActivityDetailBinding
import com.powidev.coffeshop.ViewModel.FavoriteViewModel

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private lateinit var item: ItemsModel
    private lateinit var managmentCart: ManagmentCart
    private lateinit var favoriteViewModel: FavoriteViewModel
    private lateinit var uid: String
    private var isFavorite = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        managmentCart = ManagmentCart(this)
        favoriteViewModel = ViewModelProvider(this)[FavoriteViewModel::class.java]
        uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        bundle()
        initSizeList()
        setupFavoriteLogic()
    }

    private fun initSizeList() {
        binding.apply {
            smallBtn.setOnClickListener {
                smallBtn.setBackgroundResource(R.drawable.stroke_brown_bg)
                mediumBtn.setBackgroundResource(0)
                largeBtn.setBackgroundResource(0)
            }
            mediumBtn.setOnClickListener {
                smallBtn.setBackgroundResource(0)
                mediumBtn.setBackgroundResource(R.drawable.stroke_brown_bg)
                largeBtn.setBackgroundResource(0)
            }
            largeBtn.setOnClickListener {
                smallBtn.setBackgroundResource(0)
                mediumBtn.setBackgroundResource(0)
                largeBtn.setBackgroundResource(R.drawable.stroke_brown_bg)
            }
        }
    }

    private fun bundle() {
        binding.apply {
            item = intent.getSerializableExtra("object") as ItemsModel

            Glide.with(this@DetailActivity)
                .load(item.picUrl[0])
                .into(binding.picMain)

            titleTxt.text = item.title
            descriptionTxt.text = item.description
            priceTxt.text = "$" + item.price
            ratingTxt.text = item.rating.toString()
            numberItemTxt.text = item.numberInCart.toString()

            addToCartBtn.setOnClickListener {
                item.numberInCart = numberItemTxt.text.toString().toInt()
                managmentCart.insertItems(item)
            }

            backBtn.setOnClickListener {
                finish()
            }

            plusCart.setOnClickListener {
                item.numberInCart++
                numberItemTxt.text = item.numberInCart.toString()
            }

            minusBtn.setOnClickListener {
                if (item.numberInCart > 0) {
                    item.numberInCart--
                    numberItemTxt.text = item.numberInCart.toString()
                }
            }
        }
    }

    private fun setupFavoriteLogic() {
        favoriteViewModel.isInFavorites(item, uid).observe(this) { exists ->
            isFavorite = exists
            updateFavoriteIcon()
        }

        binding.favBtn.setOnClickListener {
            if (isFavorite) {
                favoriteViewModel.removeFromFavorites(item, uid).observe(this) { success ->
                    if (success) {
                        isFavorite = false
                        updateFavoriteIcon()
                        Toast.makeText(this, "Eliminado de favoritos", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                favoriteViewModel.addToFavorites(item, uid).observe(this) { success ->
                    if (success) {
                        isFavorite = true
                        updateFavoriteIcon()
                        Toast.makeText(this, "Agregado a favoritos", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun updateFavoriteIcon() {
        if (isFavorite) {
            binding.favBtn.setImageResource(R.drawable.favorite_white)
            binding.favBtn.setColorFilter(resources.getColor(R.color.red, theme))
        } else {
            binding.favBtn.setImageResource(R.drawable.favorite_white)
            binding.favBtn.setColorFilter(resources.getColor(R.color.grey, theme))
        }
    }
}
