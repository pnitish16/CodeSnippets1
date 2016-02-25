{
		waitDialog = Helper.showPleaseWaitDialog(getActivity());
		if (VolleyHelper.isNetworkAvailable(getActivity()))
		{

			JSONObject dataobj = null;
			try
			{
				dataobj = new JSONObject();
				if (UserSession.getSession(getActivity()).isEnglish())
					dataobj.put("language_id", 1);
				else
					dataobj.put("language_id", 2);

				dataobj.put("api_key", Constants.teinet_api_key);
				// dataobj.put("page_index","1");
				// dataobj.put("page_size","10");
				// dataobj.put("news_type_id","1");
				// dataobj.put("search_keyword","lorem");
				// dataobj.put("date","2016-02-16");
				// dataobj.put("news_id","21");
				// {“language_id”: “1” “api_key”: “teiNet@prj3979”,
				// “page_index”: “1”, “page_size”: “10”,
				// “news_type_id”: “1”, “search_keyword”: “lorem”, “date”:
				// “2016-12-28”, “news_id”: “21”}
			}
			catch (JSONException e)
			{
				e.printStackTrace();
			}

			final DataInObject inObj = new DataInObject(dataobj, Constants.base_url + "News/", "NewsList");
			NetworkCall webserviceCall = new NetworkCall(getActivity());
			webserviceCall.jsonObjectPostRequest(inObj, new TaskListener()
			{
				@Override
				public void onErrorResponse(VolleyError error)
				{
					Log.v(TAG, "error " + error.getMessage());
					waitDialog.dismiss();
					if(getActivity()!= null)
						CustomDialogManager.showOkDialog(getActivity(), getString(R.string.invalid_msg));
				}

				@Override
					public void onResponse(JSONObject response)
					{
						Helper.Log("Request", inObj.getJsonObject().toString());
						Helper.Log("Response", response.toString());

						NewsItem[] arrNews;
						if (response.optString("status").equalsIgnoreCase("Success"))
					{
						try
						{
								arrNews = new GsonBuilder().create().fromJson(response.optString("newsList"), NewsItem[].class);
							if (arrNewsItems != null && arrNewsItems.size() > 0)
							{
								arrNewsItems.clear();

								arrNewsItems.addAll(decodeNewsUrls(new ArrayList<NewsItem>(Arrays.asList(arrNews))));
							}
							else
							{
								arrNewsItems = decodeNewsUrls(new ArrayList<NewsItem>(Arrays.asList(arrNews)));
								newsAdapter = new NewsListAdapter(getActivity(), arrNewsItems);
								newsAdapter.setNewsClickListener(NewsListingFragment.this);
								mRecyclerView.setAdapter(newsAdapter);

							}

							if (newsAdapter != null)
								newsAdapter.notifyDataSetChanged();

						}
						catch (Exception ex)
						{
							ex.printStackTrace();
						}

					}

					waitDialog.dismiss();
				}
			});
		}
		else
		{
			Toast.makeText(getActivity(), getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
			waitDialog.dismiss();
		}
	}
