Peasy RecyclerView
============

![Logo](https://raw.githubusercontent.com/kopihao/peasy-recyclerview/master/photoshop/peasy-recyclerview.png)

# What's good?
#### A not-so-powerful and yet easy peasy tool to setup your Android RecyclerView in a convenient way. Be practical, may not be fancy.

#### Provides a lot override methods by handling a lot boilerplate behind the scene. 
#### Yes you can override them anytime. 

### Easy hands on, easy peasy!

* Provides various RecyclerView presentation template:
    1) Vertical List
    2) Horizontal List
    3) Basic Grid
    4) Vertical Staggered Grid
    5) Horizontal Staggered Grid

* Provides handy way to setup RecyclerView presentation without boilerplate by using extending abstract class during compilation:
    1) `PeasyRecyclerView.VerticalList`
    2) `PeasyRecyclerView.HorizontalList`
    3) `PeasyRecyclerView.BasicGrid`
    4) `PeasyRecyclerView.VerticalStaggeredGrid`
    5) `PeasyRecyclerView.HorizontalStaggeredGrid`

* Provides handy way to setup RecyclerView presentation without boilerplate by using method calls during runtime:
    1) `asVerticalListView()`
    2) `asHorizontalListView()`
    3) `asGridView()`
    4) `asVerticalStaggeredGridView()`
    5) `asHorizontalStaggeredGridView()`

* Elimates needs of anonymous inner-classes for listeners by overriding methods intuitively:
    1) `onItemClick(...)`
    2) `onItemLongClick(...)`
    3) `onViewScrolled(...)`
    4) `onViewScrollStateChanged(...)`
    5) `onViewReachingEndOfList(...)`
    6) `onInterceptTouchEvent(...)`

* Retains Android promoted implement in better ways, no proprietary knowledge required to start with, you will still see these:
    1) `onCreateViewHolder(...)`
    2) `onBindViewHolder(...)`
    3) `getItemViewType(...)`

## Intuitive, Better writability & readability:
### Basic Template
* Let's set up RecyclerView as Vertical List
```java
public final class SampleRV extends PeasyRecyclerView.VerticalList<PeasyRVInbox.ModelInbox> {

        public SampleRV(@NonNull Context context, RecyclerView recyclerView, ArrayList arrayList) {
            super(context, recyclerView, arrayList);
        }

        @Override
        protected PeasyViewHolder onCreateViewHolder(LayoutInflater inflater, ViewGroup parent, int viewType) {
            // TODO Do Nothing but initializing view holder with layout_id
            return null;
        }

        @Override
        protected int getItemViewType(int position, PeasyRVInbox.ModelInbox item) {
            // TODO Do Nothing but returning view type accordingly
            return 0;
        }

        @Override
        protected void onBindViewHolder(Context context, PeasyViewHolder holder, int position, PeasyRVInbox.ModelInbox item) {
            // TODO Do Nothing but checking instance and populating item to view with view holder
        }
    }
```

### Comprehensive Template
* Elimates boilerplate implementation such as listeners
* Just override intuitively what you can find
* Popular implementation `onItemClick`, `onViewScrolled`

```java
public final class SampleRV extends PeasyRecyclerView.VerticalList<PeasyRVInbox.ModelInbox> {

        public SampleRV(@NonNull Context context, RecyclerView recyclerView, ArrayList arrayList) {
            super(context, recyclerView, arrayList);
        }

        @Override
        protected PeasyViewHolder onCreateViewHolder(LayoutInflater inflater, ViewGroup parent, int viewType) {
            // TODO Do Nothing but initializing view holder with layout_id
            return null;
        }

        @Override
        protected int getItemViewType(int position, PeasyRVInbox.ModelInbox item) {
            // TODO Do Nothing but returning view type accordingly
            return 0;
        }

        @Override
        protected void onBindViewHolder(Context context, PeasyViewHolder holder, int position, PeasyRVInbox.ModelInbox item) {
            // TODO Do Nothing but checking instance and populating item to view with view holder
        }

        @Override
        public void onItemClick(View view, int viewType, int position, PeasyRVInbox.ModelInbox item, PeasyViewHolder viewHolder) {
            // TODO Do Nothing but defining click action on PeasyRVInbox item
            super.onItemClick(view, viewType, position, item, viewHolder);
        }

        @Override
        public boolean onItemLongClick(View view, int viewType, int position, PeasyRVInbox.ModelInbox item, PeasyViewHolder viewHolder) {
            // TODO Do Nothing but defining long click action on PeasyRVInbox item
            return super.onItemLongClick(view, viewType, position, item, viewHolder);
        }

        @Override
        public void onViewScrolled(RecyclerView recyclerView, int dx, int dy) {
            // TODO Everything during RecyclerView scrolling
            super.onViewScrolled(recyclerView, dx, dy);
        }

        @Override
        public void onViewScrollStateChanged(RecyclerView recyclerView, int newState) {
            // TODO Everything after RecyclerView scroll state updated
            super.onViewScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onViewReachingEndOfList(RecyclerView recyclerView, int threshold) {
            // TODO Everything during RecyclerView scrolled to reach End of List 
           super.onViewReachingEndOfList(recyclerView, threshold);
        }

        @Override
        public void onViewInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            super.onViewInterceptTouchEvent(rv, e);
        }

    }
```

Demostration
--------  
## `MainActivity.java`
### `MainActivity`
* Activity which host  `android.support.v7.widget.RecyclerView`

## `PeasyRVInbox.java`
### `PeasyRVInbox`
* Child class of `PeasyRecyclerView` 
* Glue provided `RecyclerView` with `RecyclerView.Adapter` with desired `PeasyRecyclerView.Presentation`
### `ModelInbox`
* Serves as model T to `PeasyRVInbox<T>`
### `InboxHeaderViewHolder`,`InboxFooterViewHolder`,`InboxModelViewHolder`
* Serve as view holders to `PeasyRVInbox<T>`
### `PeasyHeaderContent<ModelInbox>`,`PeasyFooterContent<ModelInbox>`
* Serve as Coordinator Content to `PeasyRVInbox<T>` 

##### __Please checkout [source code][3]__
##### __Please checkout [live demo][4]__

Add to your Android project
--------

<img src="https://img.shields.io/bintray/v/kopihao/maven/peasy-recyclerview.svg"> <img src="https://img.shields.io/maven-central/v/com.github.kopihao/peasy-recyclerview.svg">

```gradle
dependencies {
  implementation 'com.github.kopihao:peasy-recyclerview:1.0.+'
} 
```

More Information
-------- 
##### For documentation and additional information please visit [official website][1]. 
##### For enquiries and solutions please visit [stackoverflow][2].

License
-------

    Licensed under the Apache License, Version 2.0 (the "License");
    Copyright 2013 Kopihao.MY

 [1]: https://github.com/kopihao/peasy-recyclerview/
 [2]: https://stackoverflow.com/questions/tagged/peasy-recyclerview?sort=frequent
 [3]: https://github.com/kopihao/peasy-recyclerview/tree/master/sample/src/main/java/com/kopirealm/peasyrecyclerview
 [4]: https://appetize.io/embed/yavqdtj6y0nnb0x6ru3p7yym2w?device=nexus5&scale=75&orientation=portrait&osVersion=8.1
