# trustee-finder

Java project to parse the directors and trustees from a 501(c) non-profit's Tax Form 990. 

## Purpose

IRS Tax Returns for tax-exempt corporations can be a rich source for information about different organizations. Being able to parse these returns for interesting information (such as the names of trustees and key employees) is useful for drawing connections between different tax-exempt organizations. Maybe you want to see if a non-profit has stacked its board with executives from a certain industry that benefit from its lobbying, or perhaps you want to check connections between two nonprofits - having a list of all its directors can be a useful tool for investigations. 

While IRS returns from 2011 onward are available through [Amazon Web Services' S3 platform](https://aws.amazon.com/public-datasets/irs-990/), getting information from previous years can also be useful. This is a Java project that parses an OCR-ready PDF of a tax return and returns a list of its trustees and key employees, as well as their positions. 
