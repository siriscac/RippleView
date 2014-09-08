#RippleView

View that imitates Ripple Effect on click which was introduced in Android L.

![Sample Screenshot](https://raw.github.com/siriscac/RippleView/master/Screens/Screen.gif)

#Usage

*For a working implementation, Have a look at the Sample Project - RippleViewExample*

1. Include the library as local library project.

2. Include the RippleView widget in your layout inside the FrameLayout with the View for which you want to add the Ripple Effect.

    <com.indris.material.RippleView
            android:id="@+id/btn"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            ripple:alphaFactor="0.7"
            ripple:rippleColor="#58FAAC" >
    </com.indris.material.RippleView>
    
3. In your `onCreate` method refer to the View and add 'OnClickListener' for the same.

    mButton = (RippleView) findViewById(R.id.btn);
        mButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Toast.makeText(getApplicationContext(), "Ripples Yo! :D", Toast.LENGTH_LONG).show();
			}
		});
		
#Customization

There are two attributes which are applicable to 'RippleView'.

  * `rippleColor` Color of the Ripple
  * `alphaFactor` Opacity of the Ripple
  
  * You can also set these attributes from your java code by calling setRippleColor(rippleColor, alphaFactor) *
  
# Changelog

### Current Version: 1.0

  * Initial Build
  
# Developed By

  * Muthuramakrishnan - <siriscac@gmail.com>

### Credits
  
  * [Niek Haarman](https://github.com/nhaarman)
  
# License

    Copyright 2014 Muthuramakrishnan

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
