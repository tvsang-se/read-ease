<template>
    <!-- <ul>
        <li v-for="bookmark in bookmarks" :key="bookmark.id" @click.stop="openBookmark(bookmark.id)">
            {{ bookmark.title }}
            <TableOfContent class="ml-5" v-if="bookmark.children.length" :bookmarks="bookmark.children" :viewerApis="viewerApis"></TableOfContent>
        </li>
    </ul> -->

    <ul :class="{ 'cd-accordion': (currentIndex === 0), 'cd-accordion__sub': (currentIndex > 0) }">
        <template v-for="bookmark in bookmarks" :key="++i">
            <li v-if="bookmark.children.length > 0"
                :class="{ 'ml-4': currentIndex !== 0, 'cd-accordion__item': true, 'py-2': true }">
                <input type="checkbox" class="cd-accordion__input" :id="i">
                <label :for="i" class="cd-accordion__label">
                    <v-icon size="x-small" style="cursor: pointer;">mdi-greater-than</v-icon>
                </label>
                <p @click="openBookmark(bookmark.id)" style="cursor: pointer;" class="d-inline">{{ bookmark.title }}</p>
                <TableOfContent :bookmarks="bookmark.children" :currentIndex="++i" :viewerApis="viewerApis">
                </TableOfContent>
            </li>
            <li v-else :class="{ 'ml-4': currentIndex !== 0, 'cd-accordion__item': true, 'py-2': true }"
                @click="openBookmark(bookmark.id)" style="cursor: pointer;">{{
                    bookmark.title }}</li>
        </template>
    </ul>
</template>

<script setup>
import { ref, computed } from 'vue';
const props = defineProps({
    bookmarks: Array,
    currentIndex: Number,
    viewerApis: Object
})

const bookmarks = computed(() => {
    return props.bookmarks
})

const currentIndex = computed(() => {
    return props.currentIndex
})

const viewerApis = computed(() => {
    return props.viewerApis
})

let i = ref(currentIndex.value)

// console.log("TableOfContent: ",props.viewerApis)

function openBookmark(bookmarkID) {
    this.viewerApis.getBookmarkAPIs().openBookmark(bookmarkID)
        .then(function () {
            // console.log("Bookmark with ID " + bookmarkID + " opened.");
        })
        .catch(function (error) {
            console.log(error);
        })
}
</script>
  
  
<style scoped>
* {
    font-size: 16px;
    /* padding-bottom: 8px; */
}

.cd-accordion__input {
    position: absolute;
    opacity: 0;
}

.cd-accordion__label {
    position: relative;
    /* display: flex; */
    align-items: center;
}

.cd-accordion__sub {
    display: none;
}

.cd-accordion__input:checked~.cd-accordion__sub {
    display: block;
}

ul,
li {
    list-style-type: none;
}
</style>