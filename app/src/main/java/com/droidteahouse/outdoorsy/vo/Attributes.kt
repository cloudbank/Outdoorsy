package com.droidteahouse.outdoorsy.vo

import com.squareup.moshi.Json

/*
Copyright (c) 2020 Kotlin Data Classes Generated from JSON powered by http://www.json2kotlin.com

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

For support, please feel free to contact me at https://www.linkedin.com/in/syedabsar */


data class Attributes(

        @field:Json(name = "best") val best: Boolean = false,

        @field:Json(name = "primary_image_url") val primaryImage: String?,
        @field:Json(name = "name") val name: String?,

        @field:Json(name = "description") val description: String?,
        @field:Json(name = "position") val position: Int,
        @field:Json(name = "primary") val primary: Boolean = false,
        @field:Json(name = "rental_id") val rental_id: Int = 0,
        @field:Json(name = "skip_enhance") val skip_enhance: Boolean = false,
        //@field:Json(name = "tags") val tags : List<String>,
        @field:Json(name = "url") val url: String?,
        @field:Json(name = "video") val video: Boolean = false
)