# Native Web View SDK

---

## ToDo/Progress

- **Title Capitalize**
- **WebView Callbacks**

---

## Agenda of Native SDK

The goal of this SDK is to develop a native Kotlin library that provides "Buy Now, Pay Later"
functionality to multiple brands through our financial payment service. This library will enable
these brands to integrate our services into their native applications.

The native applications will interact with our API to receive an HTML response, which will be
rendered in a text view. Part of the text view will include interactive buttons that, when clicked,
open a popup view. The popup view may either:

- Display additional HTML content with buttons, or
- Directly display a WebView within the popup.

The WebView will navigate to our CSP (Content Security Policy) website to facilitate "Buy Now, Pay
Later" transactions. Any actions performed within the WebView will be monitored, and the respective
success or failure events will trigger callbacks that are passed back to the brand's native
application for further processing.

---

## Overview

The Native Web View SDK is a Kotlin library designed to integrate "Buy Now, Pay Later" functionality into brand-native applications. This SDK simplifies the process of interacting with our financial payment services by rendering HTML content, managing WebView interactions, and providing callbacks for seamless integration.
---

## Workflow

1. **API Call**:
    - The brand-native app makes a request to the API and receives an HTML response.

2. **Text View Rendering**:
    - HTML content is displayed in a text view, including buttons for triggering popup views.

3. **Popup Display**:
    - The popup may render additional HTML or a WebView for transactions.

4. **WebView Navigation**:
    - The embedded WebView directs users to our CSP-compliant website to complete their
      transactions.

5. **Callback Handling**:
    - Actions performed in the WebView trigger success or failure callbacks, which are passed back
      to the brand-native app.

---

## Popup View Hierarchy

1. **`view` → `popupView`** (Parent View)
2. **`popupView` Subviews:**
    - `closeButton`
    - `dividerTop`
    - `overlayProductView`
    - `dividerBottom`
    - `actionButton`
3. **`overlayProductView` Subviews:**
    - `titleLabel`
    - `subtitleLabel`
    - `contentContainerView`
    - `disclosureLabel`
4. **`contentContainerView` Subviews:**
    - `headerView`
    - `contentStackView`

--- 
