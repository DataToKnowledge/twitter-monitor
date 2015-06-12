package it.dtk

import org.scalactic.ConversionCheckedTripleEquals
import org.scalatest.{ BeforeAndAfterAll, Matchers, FeatureSpec }

/**
 * Created by fabiofumarola on 12/06/15.
 */
abstract class BaseFeatureSpec extends FeatureSpec
  with Matchers
  with BeforeAndAfterAll
  with ConversionCheckedTripleEquals

